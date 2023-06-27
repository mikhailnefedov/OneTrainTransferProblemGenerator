package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class InitializeExpandInstanceSpaceParameters {
    private String experimentId;
    private List<String> featureNames;
    private double[][] transposedProjectionMatrix;
    private List<Double> targetPoint;
    private int localSearchRounds;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class LocalSearchExpandInstanceParameters {
    private List<Double> targetPoint;
    private int iterations;
    private int localSearchRounds;
}
