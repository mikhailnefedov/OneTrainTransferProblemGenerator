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
    private Mutation mutation = new AddPassengerMutation();

    public ExpandInstanceSpaceGenerationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.restTemplate = new RestTemplate();
        prelimUtils = new PrelimUtils();
    }

    @PostMapping("expand")
    void generateInstances(@RequestBody ExpandInstanceSpaceParameters parameters) {
        List<Tuple<ProblemInstance, SimpleMatrix>> instancesAndCoords = initializeKnownInstancesWithTheirCoordinates(parameters);
        List<ExpandInstanceIndividual> startPopulation =
                getNearestInstancesToTargetPoint(parameters.getTargetPoint(), instancesAndCoords)
                        .stream()
                        .map(t -> new ExpandInstanceIndividual(t.getLeft()))
                        .collect(Collectors.toList());

        for (int i = 0; i < parameters.getLocalSearchRounds(); i++) {
            startPopulation.forEach(individual -> mutation.mutate(individual));
        }

        //TODO: build better local search, ensure that new individuals are copies of previous individual
        //TODO: build coordination computation for new individual

        List<ProblemInstance> newInstances = startPopulation.stream()
                .map(ExpandInstanceIndividual::getProblemInstance)
                .peek(individual -> {
                    String instanceId = "mutated" + individual.getInstanceId();
                    InstanceFeatureDescription description = FeatureExtractor
                            .extract(instanceId,
                                    individual.getProblem());
                    String source = "ExpandInstanceController";
                    individual.setFeatureDescription(description);
                    individual.getFeatureDescription().setSource(source);

                    individual.setGeneratorName(source);
                    individual.setInstanceId(instanceId);
                }).collect(Collectors.toList());
        problemInstanceRepository.saveAll(newInstances);
    }

    private List<Tuple<ProblemInstance, SimpleMatrix>> initializeKnownInstancesWithTheirCoordinates(ExpandInstanceSpaceParameters parameters) {
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

    private PrelimResponse getPrelimData(List<ProblemInstance> problemInstances, ExpandInstanceSpaceParameters parameters) {
        PrelimRequest data = new PrelimRequest();
        data.setInstances(problemInstances);
        data.setFeatureNames(parameters.getFeatureNames());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PrelimRequest> request = new HttpEntity<>(data, headers);
        return restTemplate.postForEntity(PYTHON_BACKEND_URL + "/prelimdata", request, PrelimResponse.class)
                .getBody();
    }

    private List<Tuple<ProblemInstance, SimpleMatrix>> getNearestInstancesToTargetPoint(List<Double> targetPoint,
                                                                                        List<Tuple<ProblemInstance, SimpleMatrix>> knownInstances) {
        double targetPointX = targetPoint.get(0);
        double targetPointY = targetPoint.get(1);

        return knownInstances.stream()
                .sorted(Comparator.comparingDouble(o -> computeDistance(targetPointX, targetPointY, o.getRight())))
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

}
