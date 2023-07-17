package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

import java.util.List;

class PassengerSwap extends Swap {

    private int newRailCarriageId;
    private List<Passenger> passengersToSwap;

    public PassengerSwap(int newRailCarriageId, List<Passenger> passengers) {
        this.newRailCarriageId = newRailCarriageId;
        passengersToSwap = passengers;
    }

    @Override
    void makeSwap(Individual individual, Passenger passenger) {
        int oldRailCarriageId = individual.getPassengerRailCarriageMapping().get(passenger);
        updatePassengerRailCarriageMapping(individual, passenger, oldRailCarriageId);

        updatePassengersOfRailCarriageMapping(individual, passenger, oldRailCarriageId);
    }

    private void updatePassengerRailCarriageMapping(Individual individual, Passenger passenger, int oldRailCarriageId) {
        individual.getPassengerRailCarriageMapping().remove(passenger);
        passengersToSwap.forEach(p -> individual.getPassengerRailCarriageMapping().remove(p));

        individual.getPassengerRailCarriageMapping().put(passenger, newRailCarriageId);
        passengersToSwap.forEach(p -> individual.getPassengerRailCarriageMapping().put(p, oldRailCarriageId));
    }

    private void updatePassengersOfRailCarriageMapping(Individual individual, Passenger passenger, int oldRailCarriageId) {
        removePassengerFromPassengersOfRailCarriageMapping(individual, passenger, oldRailCarriageId);
        passengersToSwap.forEach(p -> removePassengerFromPassengersOfRailCarriageMapping(individual, passenger, newRailCarriageId));

        addPassengerToPassengersOfRailCarriageMapping(individual, passenger, newRailCarriageId);
        passengersToSwap.forEach(p -> addPassengerToPassengersOfRailCarriageMapping(individual, passenger, oldRailCarriageId));
    }

    private void removePassengerFromPassengersOfRailCarriageMapping(Individual individual, Passenger passenger, int railCarriageId) {
        for (int stationId = passenger.getInStation(); stationId < passenger.getOutStation(); stationId++) {
            individual.getPassengersOfRailCarriageOfInStation().get(stationId).get(railCarriageId).removePassenger();
        }
    }

    private void addPassengerToPassengersOfRailCarriageMapping(Individual individual, Passenger passenger, int railCarriageId) {
        for (int stationId = passenger.getInStation(); stationId < passenger.getOutStation(); stationId++) {
            individual.getPassengersOfRailCarriageOfInStation().get(stationId).get(railCarriageId).addPassenger();
        }
    }
}
