package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;

import java.util.HashMap;

public class CostComputer {

    private OneTrainTransferProblem problem;
    private RailCarriagePositionHelper railCarriagePositions;
    private HashMap<Passenger, HashMap<Integer, Double>> costOfUsingRailCarriageByPassenger;

    public CostComputer(OneTrainTransferProblem problem) {
        this.problem = problem;
        railCarriagePositions = new RailCarriagePositionHelper(problem.getTrain());
        initializeCostOfUsingRailCarriageByPassenger();
    }

    private void initializeCostOfUsingRailCarriageByPassenger() {
        costOfUsingRailCarriageByPassenger = new HashMap<>();
        for (Passenger passenger : problem.getPassengers()) {
            costOfUsingRailCarriageByPassenger.put(passenger, new HashMap<>());
            for (Integer railCarriageId : problem.getTrain().getRailCarriages().stream().map(RailCarriage::getSequenceNumber).toList()) {
                RailCarriageDistance distances =
                    railCarriagePositions.getDistanceOfRailCarriage(passenger, railCarriageId);

                double inCost = computeCost(distances.getInDistance());
                double outCost = computeCost(distances.getOutDistance());
                double cost = inCost + outCost;
                costOfUsingRailCarriageByPassenger.get(passenger).put(railCarriageId, cost);
            }
        }
    }

    public double computeCost(HashMap<Passenger, Integer> passengerRailCarriageMapping) {
        if (!isExactSolution(passengerRailCarriageMapping)) {
            return Double.NaN;
        }
        return passengerRailCarriageMapping.entrySet().stream()
            .map(entry -> costOfUsingRailCarriageByPassenger.get(entry.getKey()).get(entry.getValue()))
            .reduce(0.0, Double::sum);

    }

    private boolean isExactSolution(HashMap<Passenger, Integer> solutionMapping) {
        return solutionMapping.keySet().size() == problem.getPassengers().size();

    }

    private double computeCost(int distance) {
        return Math.pow(distance, 2);
    }
}
