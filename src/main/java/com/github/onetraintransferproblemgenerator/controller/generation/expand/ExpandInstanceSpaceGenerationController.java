package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("expandinstancespace")
public class ExpandInstanceSpaceGenerationController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";

    public ExpandInstanceSpaceGenerationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("expand")
    void generateInstances(@RequestBody ExpandInstanceSpaceParameters parameters) {
        PrelimResponse prelimData = getPrelimData(parameters);

        List<ProblemInstance> problemInstances =
                problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        List<List<Tuple<String, Double>>> featureVectors = problemInstances.stream()
                .map(instance -> instance.getFeatureDescription().getFeatureVector(parameters.getFeatureNames()))
                .toList();

        doPrelim(featureVectors, prelimData);
    }

    private PrelimResponse getPrelimData(ExpandInstanceSpaceParameters parameters) {
        List<ProblemInstance> problemInstances =
                problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());

        PrelimRequest data = new PrelimRequest();
        data.setInstances(problemInstances);
        data.setFeatureNames(parameters.getFeatureNames());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PrelimRequest> request = new HttpEntity<>(data, headers);
        return restTemplate.postForEntity(PYTHON_BACKEND_URL + "/prelimdata", request, PrelimResponse.class)
                .getBody();
    }

    private void doPrelim(List<List<Tuple<String, Double>>> featureVectors, PrelimResponse prelimData) {
        List<List<Double>> transformedFeatureVectors = featureVectors.stream()
                .map(featureVector ->
                        featureVector.stream().map(tuple -> {
                            PrelimData featurePrelimData = prelimData.getPrelimData(tuple.getLeft());
                            double tmp = boundOutliers(tuple.getRight(), featurePrelimData);
                            return boxcox(tmp, featurePrelimData.getLambda());
                        }).collect(Collectors.toList())
                )
                .toList();
        standardize(transformedFeatureVectors, prelimData.getFeatureData().size());
        transformedFeatureVectors.get(0).get(0);
    }

    private double boundOutliers(double featureValue, PrelimData prelimData) {
        if (featureValue < prelimData.getColumnMin()) {
            featureValue = prelimData.getFeatureMin();
        } else if (featureValue > prelimData.getColumnMax()) {
            featureValue = prelimData.getColumnMax();
        }
        return featureValue + 1 - prelimData.getFeatureMin();
    }

    /**
     * Box-Cox transformation
     */
    private double boxcox(double featureValue, double lambda) {
        if (lambda == 0) {
            return Math.log(featureValue);
        } else {
            return (Math.pow(featureValue, lambda) - 1.0) / lambda;
        }
    }

    private void standardize(List<List<Double>> featureVectors, int featureCount) {
        List<Double> means = new ArrayList<>();
        List<Double> stdDeviations = new ArrayList<>();

        for (int i = 0; i < featureCount; i++) {
            int column = i;
            List<Double> featureValues = featureVectors.stream().map(vector -> vector.get(column)).toList();
            double mean = featureValues.stream().mapToDouble(value -> value).average().orElse(0.0);
            means.add(mean);
            double stdDeviation = computeStandardDeviation(featureValues, mean);
            stdDeviations.add(stdDeviation);

            for (List<Double> featureVector : featureVectors) {
                featureVector.set(i, zeroMeanUnitVariance(featureVector.get(i), mean, stdDeviation));
            }
        }
    }

    private double computeStandardDeviation(List<Double> values, double mean) {
        double variance = values.stream()
                .map(value -> Math.pow(value - mean, 2))
                .reduce(Double::sum)
                .orElse(0.0) / values.size();
        variance = Double.isNaN(variance) ? 0 : variance;
        return Math.sqrt(variance);
    }

    private double zeroMeanUnitVariance(double value, double mean, double stdDeviation) {
        value -= mean;
        return value / stdDeviation;
    }

}
