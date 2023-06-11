package com.github.onetraintransferproblemgenerator.controller;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import org.ejml.simple.SimpleMatrix;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("visualization")
public class VisualizationController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";

    public VisualizationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("instancesbysource")
    void visualizeInstancesBySource(@RequestBody String experimentId) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(experimentId);
    }

    @PostMapping("visualizeinstances")
    void visualizeInstances(@RequestBody VisualizationParameters parameters) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());

        double[][] transposedFeatureMatrix = parameters.getTransposedProjectionMatrix();
        SimpleMatrix projectionMatrix = new SimpleMatrix(transposedFeatureMatrix).transpose();

        List<Tuple<ProblemInstance, SimpleMatrix>> instancesAndTheirFeatureVectors =
                createFeatureVectorsOfInstances(instances, parameters.getFeatureNames());

        List<Tuple<ProblemInstance, SimpleMatrix>> instancesAndTheirInstanceSpaceCoords =
                instancesAndTheirFeatureVectors.stream()
                        .peek(t -> t.setRight(projectionMatrix.mult(t.getRight())))
                        .toList();

        showPlot(instancesAndTheirInstanceSpaceCoords);
    }

    private List<Tuple<ProblemInstance, SimpleMatrix>> createFeatureVectorsOfInstances(List<ProblemInstance> instances, List<String> featureNames) {
        List<Tuple<String, double[]>> temp = instances.stream()
                .map(i -> new Tuple<>(i.getInstanceId(), i.getFeatureDescription().getFeatureVector(featureNames)))
                .toList();

        List<double[]> preprocessedFeatureVectors = doPreprocessingOnFeatureVectors(temp);
        return StreamUtils.zip(instances.stream(), preprocessedFeatureVectors.stream(), (instance, featureVector) -> {
            SimpleMatrix matrix = new SimpleMatrix(featureVector);
            return new Tuple<>(instance, matrix);
        }).toList();
    }

    private List<double[]> doPreprocessingOnFeatureVectors(List<Tuple<String, double[]>> instances) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Tuple<String, double[]>>> request = new HttpEntity<>(instances, headers);
        ResponseEntity<PreprocessedFeatureVectors> result2 = restTemplate.postForEntity(PYTHON_BACKEND_URL + "/preprocessing", request, PreprocessedFeatureVectors.class);

        return result2.getBody().getFeatureVectors().stream().map(row -> row.stream().mapToDouble(Double::doubleValue).toArray()).toList();
    }

    private void showPlot(List<Tuple<ProblemInstance, SimpleMatrix>> instances) {
        List<Double> x = instances.stream().map(t -> t.getRight().get(0)).toList();
        List<Double> y = instances.stream().map(t -> t.getRight().get(1)).toList();

        Plot plt = Plot.create();
        plt.plot().add(x, y, "o").label("sin");
        plt.legend().loc("upper right");
        plt.title("scatter");
        try {
            plt.show();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }

}
