package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import lombok.Data;

import java.util.List;

@Data
public class ConflictEvolutionParameters {
    private String experimentId;
    private int instanceCount;
    private String mutationName;
    private ExpandFeaturesProblemInstance instance;
    private List<ConflictCoordinate> conflictCoordinates;
}
