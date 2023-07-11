package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class FreeCapacitySwapMutation {

    private Random random;

    public void mutate(Individual individual) {
        random = new Random();
        if (individual.getPassengerRailCarriageMapping().keySet().size() > 0) {
            Passenger passenger = chooseRandomPassenger(individual);

            List<? extends Swap> swaps = createSwapsWithFreeCapacity(individual, passenger);
            if (swaps.size() > 0) {
                Swap swap = swaps.get(random.nextInt(swaps.size()));
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

}
