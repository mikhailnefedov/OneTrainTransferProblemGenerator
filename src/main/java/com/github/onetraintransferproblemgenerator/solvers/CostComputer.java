package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.HashMap;

public class CostComputer {

    private RailCarriagePositionHelper railCarriagePositions;

    public CostComputer() {

    }

    public CostComputer(OneTrainTransferProblem problem) {
        railCarriagePositions = new RailCarriagePositionHelper(problem.getTrain());
    }

    public double computeCost(HashMap<Passenger, Integer> passengerRailCarriageMapping) {
        double cost = 0;
        for (Passenger passenger : passengerRailCarriageMapping.keySet()) {
            RailCarriageDistance distances =
                    railCarriagePositions.getDistanceOfRailCarriage(passenger, passengerRailCarriageMapping.get(passenger));

            cost += computeCost(distances.getInDistance());
            cost += computeCost(distances.getOutDistance());
        }
        return cost;
    }

    public double computeCost(int startPosition, int endPosition) {
        return Math.pow(endPosition - startPosition, 2);
    }

    public double computeCost(int distance) {
        return Math.pow(distance, 2);
    }
}
