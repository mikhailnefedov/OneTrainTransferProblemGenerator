package com.github.onetraintransferproblemgenerator.controller;

import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisualizationData {
    private List<ProblemInstance> instances;
    private double[][] transposedProjectionMatrix;
    private List<String> featureNames;
}
