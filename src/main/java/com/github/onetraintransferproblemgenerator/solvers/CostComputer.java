package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.HashMap;

public class CostComputer {

    private OneTrainTransferProblem problem;
    private RailCarriagePositionHelper railCarriagePositions;

    public CostComputer(OneTrainTransferProblem problem) {
        this.problem = problem;
        railCarriagePositions = new RailCarriagePositionHelper(problem.getTrain());
    }

    public double computeCost(HashMap<Passenger, Integer> passengerRailCarriageMapping) {
        if (!isExactSolution(passengerRailCarriageMapping)) {
            return Double.NaN;
        }
        double cost = 0;
        for (Passenger passenger : passengerRailCarriageMapping.keySet()) {
            RailCarriageDistance distances =
                    railCarriagePositions.getDistanceOfRailCarriage(passenger, passengerRailCarriageMapping.get(passenger));

            cost += computeCost(distances.getInDistance());
            cost += computeCost(distances.getOutDistance());
        }
        return cost;
    }

    private boolean isExactSolution(HashMap<Passenger, Integer> solutionMapping) {
        return solutionMapping.keySet().size() == problem.getPassengers().size();

    }
    
    private double computeCost(int distance) {
        return Math.pow(distance, 2);
    }
}
