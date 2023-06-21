package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;

import java.util.HashMap;
import java.util.List;

public class FirstAvailableCarriageSolver extends OneTrainTransferSolver {

    public FirstAvailableCarriageSolver(OneTrainTransferProblem problem) {
        super(problem);
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

    private void seatPassengersInTrain(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            for (RailCarriage railCarriage : problem.getTrain().getRailCarriages()) {
                if (capacityStorage.isBoardingPossible(railCarriage.getSequenceNumber())) {
                    int railCarriageId = capacityStorage.inPassenger(railCarriage.getSequenceNumber(), passenger);
                    addToSolution(passenger, railCarriageId);
                    break;
                }
            }
        }
    }

    private void letPassengersOutOfTrain(List<Passenger> passengers) {
        passengers.forEach(p -> {
            capacityStorage.outPassenger(p);
        });
    }

}
