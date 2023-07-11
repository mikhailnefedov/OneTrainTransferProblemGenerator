package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SwapCreator {

    public static List<FreeCapacitySwap> createSwapsWithFreeCapacity(Individual individual, Passenger passenger) {
        int currentRailCarriageId = individual.getPassengerRailCarriageMapping().get(passenger);
        return individual.railCarriagesWithCapacityForRide(passenger.getInStation(), passenger.getOutStation()).stream()
                .filter(id -> !id.equals(currentRailCarriageId))
                .map(FreeCapacitySwap::new)
                .toList();
    }

    public static List<PassengerSwap> createPassengerSwaps(Individual individual, Passenger passenger) {
        Map<Integer, List<Passenger>> possiblePassengerSwaps = getPossiblePassengerSwapPartners(individual, passenger);

        return possiblePassengerSwaps.entrySet().stream().map(entry -> {
            List<Passenger> swapPassengers = entry.getValue();
            return new PassengerSwap(entry.getKey(), swapPassengers);
        }).toList();
    }

    private static Map<Integer, List<Passenger>> getPossiblePassengerSwapPartners(Individual individual, Passenger passenger) {
        return individual.getPassengerRailCarriageMapping().keySet().stream()
                .filter(p -> p.getInStation() >= passenger.getInStation() &&
                        p.getOutStation() <= passenger.getOutStation() &&
                        !p.equals(passenger))
                .collect(Collectors.groupingBy(p -> individual.getPassengerRailCarriageMapping().get(p)));
    }

}
