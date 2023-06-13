package com.github.onetraintransferproblemgenerator.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisualizationParameters {
    private String experimentId;
    private double[][] transposedProjectionMatrix;
    /**
     * must be in same format as in model
     */
    private List<String> featureNames;
    private List<Double> axisRangeX;
    private List<Double> axisRangeY;
    private int stationCount;
}
