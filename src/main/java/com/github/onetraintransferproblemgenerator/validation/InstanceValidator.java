package com.github.onetraintransferproblemgenerator.validation;

import com.github.onetraintransferproblemgenerator.exceptions.NotEnoughTrainCapacityException;
import com.github.onetraintransferproblemgenerator.exceptions.PassengerRouteException;
import com.github.onetraintransferproblemgenerator.models.*;
import lombok.SneakyThrows;

import java.util.List;

/**
 * Checks that instance can be solved (example: not to more passengers than seats, ...)
 */
public class InstanceValidator {

    @SneakyThrows
    public static void validateInstance(OneTrainTransferProblem problem) {
        validateCapacityConstraint(problem);
        validatePassengerRoute(problem);
    }

    private static void validateCapacityConstraint(OneTrainTransferProblem problem) throws NotEnoughTrainCapacityException {
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

    private static void validatePassengerRoute(OneTrainTransferProblem problem) throws PassengerRouteException {
        List<Passenger> passengers = problem.getPassengers();
        for (Passenger p : passengers) {
            if (p.getInStation() >= p.getOutStation()) {
                throw new PassengerRouteException(problem, p);
            }
        }
    }

}
