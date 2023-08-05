package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.mutations.LocalSearchMutation;
import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.helpers.MathUtils;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.PrelimInformationRepository;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import org.ejml.simple.SimpleMatrix;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Other methodology compared to .expand package
 */
@RestController
@RequestMapping("localsearch")
public class LocalSearchController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final PrelimInformationRepository prelimInformationRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";
    private final int POPULATION_COUNT = 30;

    public LocalSearchController(ProblemInstanceRepository problemInstanceRepository,
                                 PrelimInformationRepository prelimInformationRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.prelimInformationRepository = prelimInformationRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("init")
    void initExpandInstanceSpace(@RequestBody LocalSearchInitialization initParams) {
        List<ProblemInstance> problemInstances =
            problemInstanceRepository.findAllByExperimentId(initParams.getExperimentId());
        PrelimResponse prelimResponse = getPrelimData(problemInstances, initParams);
        PrelimInformation prelimInformation = new PrelimInformation(initParams.getExperimentId(), prelimResponse);
        setMeanAndDeviations(problemInstances, prelimInformation);
        prelimInformationRepository.save(prelimInformation);
    }

    private PrelimResponse getPrelimData(List<ProblemInstance> problemInstances, LocalSearchInitialization initParams) {
        PrelimRequest data = new PrelimRequest();
        data.setInstances(problemInstances);
        data.setFeatureNames(initParams.getFeatureNames());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PrelimRequest> request = new HttpEntity<>(data, headers);
        return restTemplate.postForEntity(PYTHON_BACKEND_URL + "/prelimdata", request, PrelimResponse.class)
            .getBody();
    }

    private void setMeanAndDeviations(List<ProblemInstance> problemInstances, PrelimInformation prelimInformation) {
        prelimInformation.getPrelimDataOfFeatures().entrySet().forEach(entry -> {
            String featureName = entry.getKey();

            List<Double> values = problemInstances.stream()
                .map(instance -> {
                    double rawValue = instance.getFeatureDescription().getValueByFeatureName(featureName);
                    double tmp = PrelimUtils.boundOutliers(rawValue, entry.getValue());
                    return PrelimUtils.boxCoxTransformation(tmp, entry.getValue().getLambda());
                })
                .toList();

            double mean = MathUtils.computeMean(values);
            double stdDeviation = MathUtils.computeStandardDeviation(values, mean);

            entry.getValue().setMean(mean);
            entry.getValue().setStdDeviation(stdDeviation);
        });
    }

    @PostMapping("generate")
    void generateNewInstances(@RequestBody LocalSearchGeneration localSearchGeneration) {
        PrelimInformation prelimInformation = prelimInformationRepository.findByExperimentId(localSearchGeneration.getExperimentId());
        PrelimUtils prelimUtils = new PrelimUtils(prelimInformation);
        List<LocalSearchIndividual> startPopulation = getNearestInstancesToTarget(localSearchGeneration, prelimUtils);
        LocalSearchMutation mutation = getMutation(localSearchGeneration);

        for (int i = 0; i < localSearchGeneration.getLocalSearchRounds(); i++) {
            for (int j = 0; j < startPopulation.size(); j++) {
                LocalSearchIndividual newIndividual = startPopulation.get(i).deepClone();
                mutation.mutate(newIndividual);
                newIndividual = recomputeFeatures(localSearchGeneration, prelimUtils, newIndividual);

                if (newIndividual.getFitness() <= startPopulation.get(i).getFitness()) {
                    startPopulation.set(i, newIndividual);
                }
            }
        }
        List<LocalSearchIndividual> newIndividuals = startPopulation.stream().filter(individual -> individual.getProblemInstance().getId() == null).toList();
        saveNewInstances(newIndividuals);

        System.out.println("Ended local search");
    }

    private LocalSearchMutation getMutation(LocalSearchGeneration localSearchGeneration) {
        String modulePath = "com.github.onetraintransferproblemgenerator.controller.generation.expandv2.mutations";
        try {
            Class<? extends LocalSearchMutation> cls = Class.forName(modulePath + "." + localSearchGeneration.getMutationName())
                .asSubclass(LocalSearchMutation.class);
            Constructor<? extends LocalSearchMutation> cons = cls.getConstructor();
            return cons.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null; //TODO: better error handling?
    }

    private List<LocalSearchIndividual> getNearestInstancesToTarget(LocalSearchGeneration localSearchGeneration, PrelimUtils prelimUtils) {
        List<ProblemInstance> problemInstances = problemInstanceRepository.findAllByExperimentId(localSearchGeneration.getExperimentId());

        return problemInstances.stream()
            .map(instance -> convertToLocalSearchIndividual(localSearchGeneration, prelimUtils, instance))
            .sorted(Comparator.comparing(LocalSearchIndividual::getFitness))
            .limit(POPULATION_COUNT)
            .collect(Collectors.toList());
    }

    private LocalSearchIndividual convertToLocalSearchIndividual(LocalSearchGeneration localSearchGeneration, PrelimUtils prelimUtils, ProblemInstance instance) {
        List<Tuple<String, Double>> featureVector = instance.getFeatureDescription().getFeatureVector(localSearchGeneration.getFeatureNames());
        List<Double> transformedFeatureVector = prelimUtils.doPrelimSingleFeatureVector(featureVector);
        SimpleMatrix coords = ProjectionUtils.projectSingleFeatureVector(transformedFeatureVector, localSearchGeneration.getTransposedProjectionMatrix());
        double distance = MathUtils.computeDistance(localSearchGeneration.getTargetX(), localSearchGeneration.getTargetY(), coords);
        return new LocalSearchIndividual(instance, coords, distance);
    }

    private LocalSearchIndividual recomputeFeatures(LocalSearchGeneration localSearchGeneration, PrelimUtils prelimUtils,
                                                    LocalSearchIndividual individual) {
        return convertToLocalSearchIndividual(localSearchGeneration, prelimUtils, individual.getProblemInstance());
    }

    private void saveNewInstances(List<LocalSearchIndividual> population) {
        List<ProblemInstance> newInstances = population.stream()
            .map(LocalSearchIndividual::getProblemInstance)
            .peek(individual -> {
                String instanceId = "mutated_" + individual.getInstanceId();
                individual.setInstanceId(instanceId);
                InstanceFeatureDescription description = FeatureExtractor.extract(instanceId, individual.getProblem());
                individual.setFeatureDescription(description);
                String source = "LocalSearchController";
                individual.getFeatureDescription().setSource(source);
                individual.setSource(source);
            }).collect(Collectors.toList());
        System.out.println("Generated instance count:" + newInstances.size());
        problemInstanceRepository.saveAll(newInstances);
    }

}
