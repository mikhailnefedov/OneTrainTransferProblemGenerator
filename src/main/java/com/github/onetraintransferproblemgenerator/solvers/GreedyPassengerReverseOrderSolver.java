package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Tries to solve problem greedily by abiding the reverse order of passengers by outStation and using the rail carriage
 * with minimum transfer distance.
 */
public class GreedyPassengerReverseOrderSolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;

    public GreedyPassengerReverseOrderSolver(OneTrainTransferProblem problem) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
    }

    @Override
    public HashMap<Passenger, Integer> solve() {
        List<Integer> stationIds = problem.getTrain().getStationIds().stream().sorted(Comparator.reverseOrder()).toList();

        for (int stationId : stationIds) {
            letPassengersOutOfTrain(problem.getInPassengersOfStation(stationId));
            seatPassengersInTrain(problem.getOutPassengersOfStation(stationId));
        }

        return solutionMapping;
    }

    private void letPassengersOutOfTrain(List<Passenger> passengers) {
        passengers.forEach(p -> capacityStorage.outPassenger(p));
    }

    private void seatPassengersInTrain(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            List<RailCarriageDistance> railCarriageDistances = carriagePositionHelper.getDistancesForRailCarriages(passenger);
            railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCost));
            for (RailCarriageDistance railCarriageDistance : railCarriageDistances) {
                if (capacityStorage.isBoardingPossible(railCarriageDistance.getRailCarriageId())) {
                    capacityStorage.inPassenger(railCarriageDistance.getRailCarriageId(), passenger);
                    addToSolution(passenger, railCarriageDistance.getRailCarriageId());
                    break;
                }
            }
        }
    }
}
