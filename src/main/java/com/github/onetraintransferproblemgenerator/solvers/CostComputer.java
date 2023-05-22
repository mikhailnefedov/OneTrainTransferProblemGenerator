package com.github.onetraintransferproblemgenerator.solvers;

public interface CostComputer {
    double computeCost(int startPlatform, int startPosition, int endPlatform, int endPosition);
}
