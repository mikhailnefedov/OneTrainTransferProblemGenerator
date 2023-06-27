package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.List;
import java.util.stream.Collectors;

public class IncreaseCongestionMutation implements Mutation {

    @Override
    public ExpandInstanceIndividual mutate(ExpandInstanceIndividual individual) {
        List<Tuple<Integer, Integer>> availableRides =
                individual.getStationCapacityTracker().getAvailableRides();
        if (availableRides.size() == 0) {
            return individual;
        } else {
            extendOnePassengerRide(
                    individual.getProblemInstance().getProblem().getPassengers(),
                    individual.getProblemInstance().getProblem().getTrain().getStationCount(),
                    availableRides);
        }

        return individual;
    }

    private void extendOnePassengerRide(
            List<Passenger> passengers,
            int stationCount,
            List<Tuple<Integer, Integer>> availableRides) {

        boolean rideExtended = false;
        int i = 0;
        while (!rideExtended && i < passengers.size()) {
            Passenger passenger = passengers.get(i);
            int inStation = passenger.getInStation();
            int outStation = passenger.getOutStation();
            if (inStation == 1) {
                rideExtended = tryInCreasingOutStation(passenger, availableRides);
            } else if (outStation == stationCount) {
                rideExtended = tryDecreasingInStation(passenger, availableRides);
            } else {
                rideExtended = tryDecreasingInStation(passenger, availableRides);
                rideExtended = rideExtended || tryInCreasingOutStation(passenger, availableRides);
            }
            i++;
        }
    }

    private boolean tryDecreasingInStation(Passenger passenger, List<Tuple<Integer, Integer>> availableRides) {
        int inStation = passenger.getInStation();
        List<Tuple<Integer, Integer>> rideExtensions = availableRides.stream()
                .filter(ride -> ride.getLeft() < inStation && ride.getRight() >= inStation)
                .collect(Collectors.toList());
        if (rideExtensions.size() == 0) return false;

        Tuple<Integer, Integer> rideExtension = rideExtensions.get(0);
        passenger.setInStation(rideExtension.getLeft());
        return true;
    }

    private boolean tryInCreasingOutStation(Passenger passenger, List<Tuple<Integer, Integer>> availableRides) {
        int outStation = passenger.getOutStation();
        List<Tuple<Integer, Integer>> rideExtensions = availableRides.stream()
                .filter(ride -> ride.getLeft() <= outStation && ride.getRight() > outStation)
                .collect(Collectors.toList());
        if (rideExtensions.size() == 0) return false;

        Tuple<Integer, Integer> rideExtension = rideExtensions.get(0);
        passenger.setOutStation(rideExtension.getRight());
        return true;
    }
}
