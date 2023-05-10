package com.github.onetraintransferproblemgenerator.computation;

public class SquareDistanceCost implements CostComputer {

    @Override
    public double computeCost(int startPlatform, int startPosition, int endPlatform, int endPosition) {
        if (startPlatform == endPlatform) {
            return Math.pow(endPosition - startPosition, 2);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
