package com.github.onetraintransferproblemgenerator.validation;

import com.github.onetraintransferproblemgenerator.models.*;

/**
 * Checks that instance can be solved (example: not to more passengers than seats, ...)
 */
public class InstanceValidator {

    public boolean validateInstance(OneTrainTransferProblem problem) {
        return validatePassengersAndCapacity(problem);
    }

    private boolean validatePassengersAndCapacity(OneTrainTransferProblem problem) {
        Train train = problem.getTrain();
        int trainCapacity = train.getTotalCapacity();

        int freeCapacity = trainCapacity;
        for (StationTuple station: train.getStations()) {
            int inPassengerCount = problem.getInPassengersOfStation(station.getStationId()).size();
            int outPassengerCount = problem.getOutPassengersOfStation(station.getStationId()).size();
            freeCapacity = freeCapacity - inPassengerCount + outPassengerCount;
            if (freeCapacity < 0) {
                return false;
            }
        }
        return true;
    }

}
