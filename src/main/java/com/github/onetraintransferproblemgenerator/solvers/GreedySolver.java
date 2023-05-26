package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GreedySolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;

    public GreedySolver(OneTrainTransferProblem problem) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
    }

    @Override
    public double solve() {
        List<Integer> stationIds = problem.getTrain().getStationIds();

        for (int stationId : stationIds) {
            letPassengersOutOfTrain(problem.getOutPassengersOfStation(stationId));
            seatPassengersInTrain(stationId, problem.getInPassengersOfStation(stationId));
        }

        return solutionCost;
    }

    private void letPassengersOutOfTrain(List<Passenger> passengers) {
        passengers.forEach(p -> capacityStorage.outPassenger(p));
    }

    private void seatPassengersInTrain(int stationId, List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            List<RailCarriageDistance> railCarriageDistances = getDistanceIfRailCarriageIsUsed(passenger);
            railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCombinedDistances));
            for (RailCarriageDistance railCarriageDistance : railCarriageDistances) {
                if (capacityStorage.isBoardingPossible(railCarriageDistance.getRailCarriageId())) {
                    capacityStorage.inPassenger(railCarriageDistance.getRailCarriageId(), passenger);
                    addToSolutionCost(railCarriageDistance.getInDistance());
                    addToSolutionCost(railCarriageDistance.getOutDistance());
                    break;
                }
            }
        }
    }

    private List<RailCarriageDistance> getDistanceIfRailCarriageIsUsed(Passenger passenger) {
        HashMap<Integer, Integer> inDistances =
                carriagePositionHelper.getDistancesBetweenRailCarriagesAndPosition(passenger.getInStation(), passenger.getInPosition());
        HashMap<Integer, Integer> outDistances =
                carriagePositionHelper.getDistancesBetweenRailCarriagesAndPosition(passenger.getOutStation(), passenger.getOutPosition());

        return inDistances.entrySet().stream().map(entry -> {
            int railCarriageId = entry.getKey();
            int inDistance = entry.getValue();
            int outDistance = outDistances.get(railCarriageId);
            return new RailCarriageDistance(railCarriageId, inDistance, outDistance);
        }).collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    private class RailCarriageDistance {
        private int railCarriageId;
        private int combinedDistances;
        private int inDistance;
        private int outDistance;

        public RailCarriageDistance(int railCarriageId, int inDistance, int outDistance) {
            combinedDistances = inDistance + outDistance;
            this.railCarriageId = railCarriageId;
            this.inDistance = inDistance;
            this.outDistance = outDistance;
        }
    }
}
