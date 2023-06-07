package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tries to solve problem greedily by abiding the order of passengers by inStation and using the rail carriage
 * with minimum transfer distance.
 */
public class GreedyPassengerOrderSolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;

    public GreedyPassengerOrderSolver(OneTrainTransferProblem problem) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
    }

    @Override
    public double solve() {
        List<Integer> stationIds = problem.getTrain().getStationIds();

        for (int stationId : stationIds) {
            letPassengersOutOfTrain(problem.getOutPassengersOfStation(stationId));
            seatPassengersInTrain(problem.getInPassengersOfStation(stationId));
        }

        return solutionCost;
    }

    private void letPassengersOutOfTrain(List<Passenger> passengers) {
        passengers.forEach(p -> capacityStorage.outPassenger(p));
    }

    private void seatPassengersInTrain(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            List<RailCarriageDistance> railCarriageDistances = carriagePositionHelper.getDistanceIfRailCarriageIsUsed(passenger);
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

}