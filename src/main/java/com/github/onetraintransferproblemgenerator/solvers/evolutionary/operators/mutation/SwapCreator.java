package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.RailCarriageSectionSet;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

import java.util.ArrayList;
import java.util.HashMap;
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
        int railCarriageId = individual.getRailCarriageIdOfPassenger(passenger);

        Map<Integer, List<Passenger>> possibleSwapPassengers = individual.getPassengerRailCarriageMapping().keySet().stream()
            .filter(p -> p.getInStation() >= passenger.getInStation() &&
                p.getOutStation() <= passenger.getOutStation())
            .collect(Collectors.groupingBy(p -> individual.getPassengerRailCarriageMapping().get(p)));

        possibleSwapPassengers.remove(railCarriageId);

        possibleSwapPassengers.entrySet().forEach(entry -> {

        });

        return null;

        //TODO: FAULT, DOES NOT WORK CORRECTLY, CHANGE NEEDED
    }

    private static List<List<Passenger>> getPassengerSwapCombinationsOfRailCarriage(Individual individual,
                                                                                    int railCarriageId,
                                                                                    List<Passenger> passengers,
                                                                                    int inStation,
                                                                                    int penultimateStation) {
        List<Integer> inStationsWithFreeCapacity = getInStationsWithFreeCapacity(individual, railCarriageId);

        Map<Integer, List<Passenger>> passengersOfInStation = passengers.stream().collect(Collectors.groupingBy(Passenger::getInStation));


        List<List<Passenger>> combinations = new ArrayList<>();
        for (int stationId = inStation; stationId <= penultimateStation; stationId++) {
            if (combinations.size() > 0) {
                for (List<Passenger> combination : combinations) {
                    if (combination.get(combination.size() - 1).equals(null)) {
                        if (inStationsWithFreeCapacity.contains(stationId)) {
                            combination.add(null);
                        } else {

                        }
                    }

                }
            } else {
                if (inStationsWithFreeCapacity.contains(stationId)) {
                    combinations.add(new ArrayList<>(null));
                }
                //List<Passenger>
            }
        }


        return null;
    }

    private static List<Integer> getInStationsWithFreeCapacity(Individual individual, int railCarriageId) {
        return individual.getPassengersOfRailCarriageOfInStation().get(railCarriageId).entrySet().stream()
            .filter(entry -> entry.getValue().hasFreeCapacity())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

}
