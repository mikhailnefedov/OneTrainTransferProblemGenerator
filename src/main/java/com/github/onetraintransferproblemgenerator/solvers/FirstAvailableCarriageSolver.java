package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;

import java.util.List;

public class FirstAvailableCarriageSolver extends OneTrainTransferSolver {

    public FirstAvailableCarriageSolver(OneTrainTransferProblem problem) {
        super(problem);
    }

    @Override
    public double solve() {
        List<Integer> stationIds = problem.getTrain().getStationIds();

        for (int stationId : stationIds) {
            letPassengersOutOfTrain(stationId, problem.getOutPassengersOfStation(stationId));
            seatPassengersInTrain(stationId, problem.getInPassengersOfStation(stationId));
        }

        return solutionCost;
    }

    private void seatPassengersInTrain(int stationId, List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            for (RailCarriage railCarriage : problem.getTrain().getRailCarriages()) {
                if (capacityStorage.isBoardingPossible(railCarriage.getSequenceNumber())) {
                    capacityStorage.inPassenger(railCarriage.getSequenceNumber(), passenger);
                    break;
                }
            }
        }
    }

    private void letPassengersOutOfTrain(int stationId, List<Passenger> passengers) {
        passengers.forEach(p -> {
            int railCarriageId = capacityStorage.outPassenger(p);
            int distance = problem.getTrain()
                .getDistanceBetweenPositionAndCarriagePosition(stationId, railCarriageId, p.getOutPosition());
            solutionCost += costComputer.computeCost(distance);
        });
    }

}
