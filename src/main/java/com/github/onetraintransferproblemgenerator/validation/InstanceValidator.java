package com.github.onetraintransferproblemgenerator.validation;

import com.github.onetraintransferproblemgenerator.models.*;

import java.util.List;

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
        for (Tuple<Integer, StationOperation> station: train.getStations()) {
            int inPassengerCount = problem.getInPassengersOfStation(station.getLeft()).size();
            int outPassengerCount = problem.getOutPassengersOfStation(station.getLeft()).size();
            freeCapacity = freeCapacity - inPassengerCount + outPassengerCount;
            if (freeCapacity < 0) {
                return false;
            }
        }
        return true;
    }

}
