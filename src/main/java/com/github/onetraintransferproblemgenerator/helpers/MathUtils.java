package com.github.onetraintransferproblemgenerator.helpers;

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
}
