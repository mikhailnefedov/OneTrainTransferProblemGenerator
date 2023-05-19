package com.github.onetraintransferproblemgenerator.validation;

import com.github.onetraintransferproblemgenerator.models.*;

import java.util.List;

/**
 * Checks that problem can be solved (example: not to more passengers than seats, ...)
 */
public class ProblemValidator {

    public boolean validateProblem(OneTrainTransferProblem problem) {
        return validatePassengersAndCapacity(problem);
    }

    private boolean validatePassengersAndCapacity(OneTrainTransferProblem problem) {
        Train train = problem.getTrain();
        int trainCapacity = train.getTotalCapacity();

        List<Passenger> passengers = problem.getPassengers();

        int freeCapacity = trainCapacity;
        for (Tuple<Integer, StationOperation> station: train.getStations()) {
            int inPassengerCount = getInPassengersOfStation(passengers, station.getLeft()).size();
            int outPassengerCount = getOutPassengersOfStation(passengers, station.getLeft()).size();
            freeCapacity = freeCapacity - inPassengerCount + outPassengerCount;
            if (freeCapacity < 0) {
                return false;
            }
        }
        return true;
    }

    private List<Passenger> getInPassengersOfStation(List<Passenger> passengers, int stationId) {
        return passengers.stream().filter(p -> p.getInStation() == stationId).toList();
    }

    private List<Passenger> getOutPassengersOfStation(List<Passenger> passengers, int stationId) {
        return passengers.stream().filter(p -> p.getOutStation() == stationId).toList();
    }
}
