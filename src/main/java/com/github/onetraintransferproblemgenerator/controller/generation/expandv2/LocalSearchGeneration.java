package com.github.onetraintransferproblemgenerator.controller.generation.expandv2;

import lombok.Data;

import java.util.List;

@Data
public class LocalSearchGeneration {
    private String experimentId;
    private List<String> featureNames;
    private double[][] transposedProjectionMatrix;
    private double targetX;
    private double targetY;
    private String mutationName;
    private int iterations;
    private int localSearchRounds;
}
