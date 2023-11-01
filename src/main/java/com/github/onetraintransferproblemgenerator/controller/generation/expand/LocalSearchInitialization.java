package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import lombok.Data;

import java.util.List;

@Data
public class LocalSearchInitialization {
    /**
     * Dataset that will be examined
     */
    private String experimentId;
    /**
     *
     */
    private List<String> featureNames;
    /**
     *
     */
    private double[][] transposedProjectionMatrix;
}
