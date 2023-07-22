package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

import java.util.ArrayList;
import java.util.Collections;
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
        Map<Integer, List<List<Passenger>>> possiblePassengerSwaps = getPossiblePassengerSwapPartners(individual, passenger);
        List<PassengerSwap> passengerSwaps = new ArrayList<>();

        possiblePassengerSwaps.forEach((key, value) ->
            value.forEach(passengerList -> passengerSwaps.add(new PassengerSwap(key, passengerList)))
        );

        return passengerSwaps;
    }

    private static Map<Integer, List<List<Passenger>>> getPossiblePassengerSwapPartners(Individual individual, Passenger passenger) {
        int railCarriageId = individual.getRailCarriageIdOfPassenger(passenger);

        Map<Integer, List<Passenger>> possibleSwapPassengers = individual.getPassengerRailCarriageMapping().keySet().stream()
            .filter(p -> p.getInStation() >= passenger.getInStation() &&
                p.getOutStation() <= passenger.getOutStation())
            .collect(Collectors.groupingBy(p -> individual.getPassengerRailCarriageMapping().get(p)));

        possibleSwapPassengers.remove(railCarriageId);

        possibleSwapPassengers.entrySet().forEach(entry -> {

        });

        return possibleSwapPassengers.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey(),
                entry -> getPassengerSwapCombinationsOfRailCarriage(individual, entry.getKey(), entry.getValue(), passenger.getInStation(), passenger.getOutStation() - 1)));
    }

    private static List<List<Passenger>> getPassengerSwapCombinationsOfRailCarriage(Individual individual,
                                                                                    int railCarriageId,
                                                                                    List<Passenger> passengers,
                                                                                    int inStation,
                                                                                    int penultimateStation) {
        List<Integer> inStationsWithFreeCapacity = getInStationsWithFreeCapacity(individual, inStation, penultimateStation, railCarriageId);

        Map<Integer, List<Passenger>> passengersOfInStation = passengers.stream().collect(Collectors.groupingBy(Passenger::getInStation));
        for (int i = inStation; i <= penultimateStation; i++) {
            if (!passengersOfInStation.containsKey(i)) {
                passengersOfInStation.put(i, new ArrayList<>());
            }
        }

        List<List<Passenger>> combinations = new ArrayList<>();
        for (int stationId = inStation; stationId <= penultimateStation; stationId++) {
            if (combinations.size() > 0) {
                List<List<Passenger>> newCombinations = new ArrayList<>();
                for (List<Passenger> combination : combinations) {
                    if (combination.get(combination.size() - 1) == null) {
                        if (inStationsWithFreeCapacity.contains(stationId)) {
                            List<Passenger> newCombination = new ArrayList<>(combination);
                            newCombination.add(null);
                            newCombinations.add(newCombination);
                        }
                        List<Passenger> laterPassengers = passengersOfInStation.get(stationId);
                        for (Passenger p : laterPassengers) {
                            List<Passenger> newCombination = new ArrayList<>(combination);
                            newCombination.add(p);
                            newCombinations.add(newCombination);
                        }
                    } else if (combination.get(combination.size() - 1).getOutStation() == stationId) {
                        if (inStationsWithFreeCapacity.contains(stationId)) {
                            List<Passenger> newCombination = new ArrayList<>(combination);
                            newCombination.add(null);
                            newCombinations.add(newCombination);
                        }
                        List<Passenger> laterPassengers = passengersOfInStation.get(stationId);
                        for (Passenger p : laterPassengers) {
                            List<Passenger> newCombination = new ArrayList<>(combination);
                            newCombination.add(p);
                            newCombinations.add(newCombination);
                        }
                    }
                }
                combinations = newCombinations;
            } else {
                if (inStationsWithFreeCapacity.contains(stationId)) {
                    List<Passenger> newCombination = new ArrayList<>();
                    newCombination.add(null);
                    combinations.add(newCombination);
                }
                List<Passenger> laterPassengers = passengersOfInStation.get(stationId);
                for (Passenger p : laterPassengers) {
                    List<Passenger> newCombination = new ArrayList<>();
                    newCombination.add(p);
                    combinations.add(newCombination);
                }
            }
        }
        cleanPassengerCombinations(combinations);
        return combinations;
    }

    private static List<Integer> getInStationsWithFreeCapacity(Individual individual, int inStation,
                                                               int penultimateStation, int railCarriageId) {

        return individual.getPassengersOfRailCarriageOfInStation().entrySet().stream()
            .filter(entry -> entry.getKey() >= inStation && entry.getKey() <= penultimateStation)
            .filter(entry -> entry.getValue().get(railCarriageId).hasFreeCapacity())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private static void cleanPassengerCombinations(List<List<Passenger>> passengerCombinations) {
        passengerCombinations.forEach(passengers -> passengers.removeAll(Collections.singleton(null)));
    }

}
