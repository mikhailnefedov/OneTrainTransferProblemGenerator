package com.github.onetraintransferproblemgenerator.validation;

import com.github.onetraintransferproblemgenerator.exceptions.NotEnoughTrainCapacityException;
import com.github.onetraintransferproblemgenerator.models.*;
import lombok.SneakyThrows;

/**
 * Checks that instance can be solved (example: not to more passengers than seats, ...)
 */
public class InstanceValidator {

    @SneakyThrows
    public void validateInstance(OneTrainTransferProblem problem) {
        validatePassengersAndCapacity(problem);
    }

    private void validatePassengersAndCapacity(OneTrainTransferProblem problem) throws NotEnoughTrainCapacityException {
        Train train = problem.getTrain();

        int freeCapacity = train.getTotalCapacity();
        for (StationTuple station: train.getStations()) {
            int inPassengerCount = problem.getInPassengersOfStation(station.getStationId()).size();
            int outPassengerCount = problem.getOutPassengersOfStation(station.getStationId()).size();
            freeCapacity = freeCapacity - inPassengerCount + outPassengerCount;
            if (freeCapacity < 0) {
                throw new NotEnoughTrainCapacityException(problem, station, freeCapacity);
            }
        }
    }

}
