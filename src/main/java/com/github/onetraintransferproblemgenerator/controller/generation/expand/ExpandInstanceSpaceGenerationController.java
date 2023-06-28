package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import org.ejml.simple.SimpleMatrix;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RestController
@RequestMapping("expandinstancespace")
public class ExpandInstanceSpaceGenerationController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";
    private PrelimUtils prelimUtils;
    private final int POPULATION_COUNT = 20;
    private Mutation mutation = new IncreaseCongestionMutation();
    private InitializeExpandInstanceSpaceParameters instanceSpaceParameters;
    private List<ExpandInstanceIndividual> individuals;

    public ExpandInstanceSpaceGenerationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.restTemplate = new RestTemplate();
        prelimUtils = new PrelimUtils();
    }

    @PostMapping("init")
    void initExpandInstanceSpace(@RequestBody InitializeExpandInstanceSpaceParameters parameters) {
        List<Tuple<ProblemInstance, SimpleMatrix>> instancesAndCoords = initializeKnownInstancesWithTheirCoordinates(parameters);
        individuals = instancesAndCoords.stream()
                .map(t -> new ExpandInstanceIndividual(t.getLeft(), t.getRight()))
                .collect(Collectors.toList());
        instanceSpaceParameters = parameters;
    }

    @PostMapping("localsearch")
    void generateInstancesThroughLocalSearch(@RequestBody LocalSearchExpandInstanceParameters parameters) {
        for (int i = 0; i < parameters.getIterations(); i++) {
            localSearchIteration(parameters.getTargetPoint(), parameters.getLocalSearchRounds());
        }
    }

    private void localSearchIteration(List<Double> targetPoint, int localSearchRounds) {
        List<ExpandInstanceIndividual> startPopulation =
                getNearestInstancesToTargetPoint(targetPoint);

        for (int i = 0; i < localSearchRounds; i++) {
            for (int j = 0; j < startPopulation.size(); j++) {
                ExpandInstanceIndividual newIndividual = startPopulation.get(i).deepClone();
                mutation.mutate(newIndividual);
                setCoordinatesAndDistance(newIndividual, targetPoint);

                if (newIndividual.getFitness() <= startPopulation.get(i).getFitness()) {
                    startPopulation.set(i, newIndividual);
                }
            }
        }
        List<ExpandInstanceIndividual> newIndividuals = startPopulation.stream().filter(individual -> individual.getProblemInstance().getId() == null).toList();
        individuals.addAll(newIndividuals);
        saveNewInstances(newIndividuals);
    }

    private List<Tuple<ProblemInstance, SimpleMatrix>> initializeKnownInstancesWithTheirCoordinates(InitializeExpandInstanceSpaceParameters parameters) {
        List<ProblemInstance> problemInstances =
                problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        PrelimResponse prelimData = getPrelimData(problemInstances, parameters);

        List<List<Tuple<String, Double>>> featureVectors = problemInstances.stream()
                .map(instance -> instance.getFeatureDescription().getFeatureVector(parameters.getFeatureNames()))
                .toList();

        List<List<Double>> transformedFeatureVectors = prelimUtils.doPrelim(featureVectors, prelimData);
        List<SimpleMatrix> coords = ProjectionUtils.projectFeatureVectors(transformedFeatureVectors, parameters.getTransposedProjectionMatrix());

        return StreamUtils
                .zip(problemInstances.stream(), coords.stream(), (BiFunction<ProblemInstance, SimpleMatrix, Tuple<ProblemInstance, SimpleMatrix>>) Tuple::new)
                .toList();
    }

    private PrelimResponse getPrelimData(List<ProblemInstance> problemInstances, InitializeExpandInstanceSpaceParameters parameters) {
        PrelimRequest data = new PrelimRequest();
        data.setInstances(problemInstances);
        data.setFeatureNames(parameters.getFeatureNames());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PrelimRequest> request = new HttpEntity<>(data, headers);
        return restTemplate.postForEntity(PYTHON_BACKEND_URL + "/prelimdata", request, PrelimResponse.class)
                .getBody();
    }

    private List<ExpandInstanceIndividual> getNearestInstancesToTargetPoint(List<Double> targetPoint) {
        double targetPointX = targetPoint.get(0);
        double targetPointY = targetPoint.get(1);

        return individuals.stream()
                .peek(individual -> individual.setFitness(computeDistance(targetPointX, targetPointY, individual.getCoordinates())))
                .sorted(Comparator.comparing(ExpandInstanceIndividual::getFitness))
                .limit(POPULATION_COUNT)
                .collect(Collectors.toList());
    }

    private double computeDistance(double targetPointX, double targetPointY, SimpleMatrix instanceCoords) {
        double instanceX = instanceCoords.get(0, 0);
        double instanceY = instanceCoords.get(1, 0);

        double deltaX = Math.abs(instanceX - targetPointX);
        double deltaY = Math.abs(instanceY - targetPointY);

        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    private void setCoordinatesAndDistance(ExpandInstanceIndividual individual,
                                           List<Double> targetPoint) {
        List<Tuple<String, Double>> featureVector = individual.getProblemInstance().getFeatureDescription().getFeatureVector(instanceSpaceParameters.getFeatureNames());

        List<Double> transformedFeatureVector = prelimUtils.doPrelimOnSingleFeatureVector(featureVector);
        SimpleMatrix coords = ProjectionUtils.projectSingleFeatureVector(transformedFeatureVector, instanceSpaceParameters.getTransposedProjectionMatrix());

        individual.setCoordinates(coords);

        double distance = computeDistance(targetPoint.get(0), targetPoint.get(1), coords);
        individual.setFitness(distance);
    }

    private void saveNewInstances(List<ExpandInstanceIndividual> population) {
        List<ProblemInstance> newInstances = population.stream()
                .map(ExpandInstanceIndividual::getProblemInstance)
                .peek(individual -> {
                    String instanceId = "mutated_" + individual.getInstanceId();
                    individual.setInstanceId(instanceId);
                    InstanceFeatureDescription description = FeatureExtractor
                            .extract(instanceId,
                                    individual.getProblem());
                    individual.setFeatureDescription(description);
                    String source = "ExpandInstanceController";
                    individual.getFeatureDescription().setSource(source);
                    individual.setGeneratorName(source);
                }).collect(Collectors.toList());
        problemInstanceRepository.saveAll(newInstances);
        System.out.println("Ended local search");
    }

}
