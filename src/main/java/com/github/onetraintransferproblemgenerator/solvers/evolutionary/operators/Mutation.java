package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Mutation {

    private Random random;

    public void mutate(Individual individual) {
        random = new Random();
        if (individual.getPassengerRailCarriageMapping().keySet().size() > 0) {
            Passenger passenger = chooseRandomPassenger(individual);

            List<? extends SwapPartner> swaps = createSwapsWithFreeCapacity(individual, passenger);
            if (swaps.size() > 0) {
                SwapPartner swap = swaps.get(random.nextInt(swaps.size()));
                swap.makeSwap(individual, passenger);
            }
        }
    }

    private Passenger chooseRandomPassenger(Individual individual) {
        List<Passenger> passengers = individual.getPassengerRailCarriageMapping().keySet().stream().toList();

        return passengers.get(random.nextInt(passengers.size()));
    }

    private List<FreeCapacitySwap> createSwapsWithFreeCapacity(Individual individual, Passenger passenger) {
        int currentRailCarriageId = individual.getPassengerRailCarriageMapping().get(passenger);
        return individual.railCarriagesWithCapacityForRide(passenger.getInStation(), passenger.getOutStation()).stream()
                .filter(id -> !id.equals(currentRailCarriageId))
                .map(FreeCapacitySwap::new)
                .toList();
    }

    private void getPassengerSwaps(Individual individual, Passenger passenger) {
        Map<Integer, List<Passenger>> possiblePassengersOfRailCarriage = getPossiblePassengerSwapPartners(individual, passenger);

        for (Integer railCarriageId : possiblePassengersOfRailCarriage.keySet()) {
            List<Passenger> passengers = possiblePassengersOfRailCarriage.get(railCarriageId);
            //TODO: Make it possible to swap with passengers too
        }
    }

    private Map<Integer, List<Passenger>> getPossiblePassengerSwapPartners(Individual individual, Passenger passenger) {
        return individual.getPassengerRailCarriageMapping().keySet().stream()
                .filter(p -> p.getInStation() >= passenger.getInStation() &&
                        p.getOutStation() <= passenger.getOutStation() &&
                        !p.equals(passenger))
                .collect(Collectors.groupingBy(p -> individual.getPassengerRailCarriageMapping().get(p)));
    }

    private abstract class SwapPartner {

        abstract void makeSwap(Individual individual, Passenger passenger);

    }

    private class FreeCapacitySwap extends SwapPartner {

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

    private class PassengerSwap extends SwapPartner {

        @Override
        void makeSwap(Individual individual, Passenger passenger) {

        }
    }
}
