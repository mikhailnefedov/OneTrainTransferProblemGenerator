package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation.FreeCapacitySwapMutation;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation.Mutation;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;

@Data
public class SolverConfiguration {
    private int populationSize = 50;
    private int parentsCount = 20;
    private int childrenCount = 50;
    private int generationCount = 200;
    private double mutationRate = 0.3;
    private String mutationType = FreeCapacitySwapMutation.class.getCanonicalName();

    public Mutation getMutation() {
        try {
            Class<? extends Mutation> mutationClass = Class.forName(mutationType).asSubclass(Mutation.class);
            return mutationClass.getConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return new FreeCapacitySwapMutation();
        }
    }

    public String getConfigurationAsString() {
        int lastPointIndex = mutationType.lastIndexOf(".");
        String mutationSimpleName = mutationType.substring(lastPointIndex + 1);
        return String.format("%d-%d-%d-%d-%f-%s",
            populationSize, parentsCount, childrenCount, generationCount, mutationRate, mutationSimpleName);
    }
}
