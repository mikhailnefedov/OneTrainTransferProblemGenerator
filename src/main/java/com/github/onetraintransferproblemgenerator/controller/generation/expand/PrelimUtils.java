package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrelimUtils {

    List<Double> means;
    List<Double> stdDeviations;


    public PrelimUtils() {

    }

    public List<List<Double>> doPrelim(List<List<Tuple<String, Double>>> featureVectors, PrelimResponse prelimData) {
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
        return transformedFeatureVectors;
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
        means = new ArrayList<>();
        stdDeviations = new ArrayList<>();

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
