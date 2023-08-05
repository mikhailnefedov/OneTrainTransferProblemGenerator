package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;

import java.util.List;
import java.util.stream.Collectors;

public class PrelimUtils {

    private final PrelimInformation prelimInformation;

    public PrelimUtils(PrelimInformation prelimInformation) {
        this.prelimInformation = prelimInformation;
    }

    public List<Double> doPrelimSingleFeatureVector(List<Tuple<String, Double>> featureVector) {
        return featureVector.stream()
            .map(tuple -> {
                PrelimData prelimData =
                    prelimInformation.getPrelimDataOfFeatures().get(tuple.getLeft());
                double tmp = boundOutliers(tuple.getRight(), prelimData);
                tmp = boxCoxTransformation(tmp, prelimData.getLambda());
                return zeroMeanUnitVariance(tmp, prelimData.getMean(), prelimData.getStdDeviation());
            })
            .collect(Collectors.toList());
    }

    public static double boundOutliers(double featureValue, PrelimData prelimData) {
        if (featureValue < prelimData.getColumnMin()) {
            featureValue = prelimData.getFeatureMin();
        } else if (featureValue > prelimData.getColumnMax()) {
            featureValue = prelimData.getColumnMax();
        }
        return featureValue + 1 - prelimData.getFeatureMin();
    }

    public static double boxCoxTransformation(double featureValue, double lambda) {
        if (lambda == 0) {
            return Math.log(featureValue);
        } else {
            return (Math.pow(featureValue, lambda) - 1.0) / lambda;
        }
    }

    private double zeroMeanUnitVariance(double value, double mean, double stdDeviation) {
        value -= mean;
        return value / stdDeviation;
    }
}
