package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.HashMap;

public abstract class OneTrainTransferSolver {

    protected OneTrainTransferProblem problem;
    protected SeatReservationStorage capacityStorage;
    protected HashMap<Passenger, Integer> solutionMapping;

    public OneTrainTransferSolver(OneTrainTransferProblem problem) {
        this.problem = problem;
        capacityStorage = new SeatReservationStorage(problem.getTrain());
        solutionMapping = new HashMap<>();
    }

    public abstract HashMap<Passenger, Integer> solve();

    protected void addToSolution(Passenger passenger, int railCarriageId) {
        solutionMapping.put(passenger, railCarriageId);
    }
}
