package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RandomPassengerOrderSolver extends OneTrainTransferSolver {

    private List<Integer> railCarriageIds;

    public RandomPassengerOrderSolver(OneTrainTransferProblem problem) {
        super(problem);
        railCarriageIds = problem.getTrain().getRailCarriages().stream()
            .map(RailCarriage::getSequenceNumber)
            .collect(Collectors.toList());
    }

    @Override
    public HashMap<Passenger, Integer> solve() {
        List<Integer> stationIds = problem.getTrain().getStationIds();

        for (int stationId : stationIds) {
            letPassengersOutOfTrain(problem.getOutPassengersOfStation(stationId));
            seatPassengersInTrain(problem.getInPassengersOfStation(stationId));
        }

        return solutionMapping;
    }

    private void letPassengersOutOfTrain(List<Passenger> passengers) {
        passengers.forEach(p -> capacityStorage.outPassenger(p));
    }

    private void seatPassengersInTrain(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            Collections.shuffle(railCarriageIds);
            for (Integer railCarriageId : railCarriageIds) {
                if (capacityStorage.isBoardingPossible(railCarriageId)) {
                    capacityStorage.inPassenger(railCarriageId, passenger);
                    addToSolution(passenger, railCarriageId);
                    break;
                }
            }
        }
    }
}
