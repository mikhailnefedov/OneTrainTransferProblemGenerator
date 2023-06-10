package com.github.onetraintransferproblemgenerator.controller;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import org.ejml.simple.SimpleMatrix;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("visualization")
public class VisualizationController {

    private final ProblemInstanceRepository problemInstanceRepository;

    public VisualizationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
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
        return instances.stream()
                .map(i -> {
                    double[] featureVector = i.getFeatureDescription().getFeatureVector(featureNames);
                    return new Tuple<>(i, new SimpleMatrix(featureVector));
                })
                .toList();
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
