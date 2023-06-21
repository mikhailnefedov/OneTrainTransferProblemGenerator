package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import org.ejml.simple.SimpleMatrix;

import java.util.List;

public class ProjectionUtils {

    public static List<SimpleMatrix> projectFeatureVector(List<List<Double>> featureVectors, double[][] transposedProjectionMatrix) {
        SimpleMatrix projectionMatrix = new SimpleMatrix(transposedProjectionMatrix).transpose();

        return featureVectors.stream().map(v -> {
            SimpleMatrix featureMatrix = new SimpleMatrix(v.stream().mapToDouble(d -> d).toArray());
            return projectionMatrix.mult(featureMatrix);
        }).toList();
    }
}
