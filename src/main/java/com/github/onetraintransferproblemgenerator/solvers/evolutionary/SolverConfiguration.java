package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import lombok.Data;

@Data
public class SolverConfiguration {
    private int populationSize = 50;
    private int parentsCount = 20;
    private int childrenCount = 50;
    private int generationCount = 200;
    private double mutationRate = 0.3;
    private int generationsWithoutImprovement = 50;


    public String getConfigurationAsString() {
        return String.format("%d-%d-%d-%d-%f",
            populationSize, parentsCount, childrenCount, generationCount, mutationRate);
    }
}
