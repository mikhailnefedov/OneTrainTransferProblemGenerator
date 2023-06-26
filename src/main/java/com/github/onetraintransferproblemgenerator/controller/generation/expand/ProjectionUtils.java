package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import org.ejml.simple.SimpleMatrix;

import java.util.List;

public class ProjectionUtils {

    public static List<SimpleMatrix> projectFeatureVectors(List<List<Double>> featureVectors,
                                                           double[][] transposedProjectionMatrix) {
        return featureVectors.stream().map(v -> projectSingleFeatureVector(v, transposedProjectionMatrix)).toList();
    }

    public static SimpleMatrix projectSingleFeatureVector(List<Double> featureVector,
                                                          double[][] transposedProjectionMatrix) {
        SimpleMatrix projectionMatrix = new SimpleMatrix(transposedProjectionMatrix).transpose();

        SimpleMatrix featureMatrix = new SimpleMatrix(featureVector.stream().mapToDouble(d -> d).toArray());
        return projectionMatrix.mult(featureMatrix);
    }
}
