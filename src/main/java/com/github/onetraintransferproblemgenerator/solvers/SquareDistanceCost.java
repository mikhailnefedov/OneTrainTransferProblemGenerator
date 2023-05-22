package com.github.onetraintransferproblemgenerator.solvers;

public class SquareDistanceCost implements CostComputer {

    @Override
    public double computeCost(int startPosition, int endPosition) {
        return Math.pow(endPosition - startPosition, 2);
    }

    @Override
    public double computeCost(int distance) {
        return Math.pow(distance, 2);
    }
}
