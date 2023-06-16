package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;

public abstract class OneTrainTransferSolver {

    protected OneTrainTransferProblem problem;
    protected SeatReservationStorage capacityStorage;
    protected CostComputer costComputer;
    protected double solutionCost = 0;

    public OneTrainTransferSolver(OneTrainTransferProblem problem) {
        costComputer = new CostComputer();
        this.problem = problem;
        capacityStorage = new SeatReservationStorage(problem.getTrain());
    }

    public abstract double solve();

    protected void addToSolutionCost(int stationId, int railCarriageId, int passengerPosition) {
        int distance = problem.getTrain()
                .getDistanceBetweenPositionAndCarriagePosition(stationId, railCarriageId, passengerPosition);
        solutionCost += costComputer.computeCost(distance);
    }

    /**
     * @param distance simple distance without prior use of cost computation
     */
    protected void addToSolutionCost(int distance) {
        solutionCost += costComputer.computeCost(distance);
    }
}
