package com.github.onetraintransferproblemgenerator.helpers;

import org.ejml.simple.SimpleMatrix;

import java.util.List;

public class MathUtils {

    public static double computeMean(List<Double> values) {
        return values.stream().mapToDouble(value -> value).average().orElse(0.0);
    }

    public static double computeStandardDeviation(List<Double> values, double mean) {
        double variance = values.stream()
            .map(value -> Math.pow(value - mean, 2))
            .reduce(Double::sum)
            .orElse(0.0) / values.size();
        variance = Double.isNaN(variance) ? 0 : variance;
        return Math.sqrt(variance);
    }

    public static double computeDistance(double targetPointX, double targetPointY, SimpleMatrix instanceCoords) {
        double instanceX = instanceCoords.get(0, 0);
        double instanceY = instanceCoords.get(1, 0);

        double deltaX = Math.abs(instanceX - targetPointX);
        double deltaY = Math.abs(instanceY - targetPointY);

        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }
}
