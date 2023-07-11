package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

class FreeCapacitySwap extends Swap {

    private int newRailCarriageId;

    public FreeCapacitySwap(int railCarriageId) {
        this.newRailCarriageId = railCarriageId;
    }

    @Override
    void makeSwap(Individual individual, Passenger passenger) {
        int lastRailCarriageId = individual.getPassengerRailCarriageMapping().get(passenger);
        individual.getPassengerRailCarriageMapping().remove(passenger);
        individual.getPassengerRailCarriageMapping().put(passenger, newRailCarriageId);

        for (int stationId = passenger.getInStation(); stationId < passenger.getOutStation(); stationId++) {
            individual.getPassengersOfRailCarriageOfInStation().get(stationId).get(lastRailCarriageId).removePassenger(passenger);
            individual.getPassengersOfRailCarriageOfInStation().get(stationId).get(newRailCarriageId).addPassenger(passenger);
        }
    }
}
