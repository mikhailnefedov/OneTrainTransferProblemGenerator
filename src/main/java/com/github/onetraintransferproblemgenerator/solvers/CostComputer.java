package com.github.onetraintransferproblemgenerator.solvers;

public interface CostComputer {
    double computeCost(int startPosition, int endPosition);
    double computeCost(int distance);
}
