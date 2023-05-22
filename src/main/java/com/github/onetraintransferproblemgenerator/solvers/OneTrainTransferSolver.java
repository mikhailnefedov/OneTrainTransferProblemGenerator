package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;

public abstract class OneTrainTransferSolver {

    protected OneTrainTransferProblem problem;
    protected SeatReservationStorage capacityStorage;
    protected CostComputer costComputer;
    protected double solutionCost = 0;

    public OneTrainTransferSolver(OneTrainTransferProblem problem) {
        this.problem = problem;
        capacityStorage = new SeatReservationStorage(problem.getTrain());
        costComputer = new SquareDistanceCost();
    }

    public abstract double solve();
}
