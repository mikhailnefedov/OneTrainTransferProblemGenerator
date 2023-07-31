package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

import java.util.*;
import java.util.stream.Collectors;

public class SwapCreator {

    public static List<FreeCapacitySwap> createSwapsWithFreeCapacity(Individual individual, Passenger passenger) {
        int currentRailCarriageId = individual.getPassengerRailCarriageMapping().get(passenger);
        return individual.railCarriagesWithCapacityForRide(passenger.getInStation(), passenger.getOutStation()).stream()
            .filter(id -> !id.equals(currentRailCarriageId))
            .map(FreeCapacitySwap::new)
            .toList();
    }

    public static List<PassengerSwap> createPassengerSwap(Individual individual, Passenger passenger) {
        Tuple<Integer, List<Passenger>> combination = getPassengerSwapCombination(individual, passenger);
        if (combination != null) {
            return new ArrayList<>(List.of(new PassengerSwap(combination.getLeft(), combination.getRight())));
        }
        else return new ArrayList<>();
    }

    private static Tuple<Integer, List<Passenger>> getPassengerSwapCombination(Individual individual, Passenger passenger) {
        int railCarriageId = individual.getRailCarriageIdOfPassenger(passenger);

        List<Passenger> possibleSwapPassengers = individual.getPassengerRailCarriageMapping().keySet().stream()
            .filter(p -> p.getInStation() >= passenger.getInStation() &&
                p.getOutStation() <= passenger.getOutStation() &&
                individual.getPassengerRailCarriageMapping().get(p) != railCarriageId)
            .toList();
        if (possibleSwapPassengers.size() > 0) {
            Random random = new Random();
            Passenger possibleSwapPartner = possibleSwapPassengers.get(random.nextInt(possibleSwapPassengers.size()));
            List<Passenger> otherPassengersOfRailCarriage = individual.getPassengerRailCarriageMapping().entrySet().stream().filter(entry -> entry.getValue() == railCarriageId).map(entry -> entry.getKey()).toList();

            return new Tuple<>(railCarriageId, tryToCreatePassengerSwapCombination(individual, railCarriageId, possibleSwapPartner, otherPassengersOfRailCarriage, passenger.getInStation(), passenger.getOutStation() - 1));
        } else {
            return null;
        }
    }

    private static List<Passenger> tryToCreatePassengerSwapCombination(Individual individual, int railCarriageId,
                                                                       Passenger swapPartner, List<Passenger> otherPassengersOfRailCarriageId,
                                                                       int inStation, int penultimateStation) {
        List<Integer> inStationsWithFreeCapacity = getInStationsWithFreeCapacity(individual, inStation, penultimateStation, railCarriageId);
        otherPassengersOfRailCarriageId = otherPassengersOfRailCarriageId.stream().filter(p -> p.getOutStation() <= swapPartner.getInStation() || p.getInStation() >= swapPartner.getOutStation()).toList();
        Map<Integer, List<Passenger>> passengersOfInStation = otherPassengersOfRailCarriageId.stream().collect(Collectors.groupingBy(Passenger::getInStation));

        List<Passenger> swapPartners = new ArrayList<>(List.of(swapPartner));
        Random random = new Random();
        if (swapPartner.getInStation() != inStation) {
            List<List<Passenger>> combinationsBeforeInStation = getCombinations(inStation, swapPartner.getInStation() - 1, inStationsWithFreeCapacity, passengersOfInStation);
            if (combinationsBeforeInStation.size() == 0) {
                return new ArrayList<>();
            }
            List<Passenger> passengers = combinationsBeforeInStation.get(random.nextInt(combinationsBeforeInStation.size()));
            swapPartners.addAll(passengers);
        }

        if (swapPartner.getOutStation() != penultimateStation + 1) {
            List<List<Passenger>> combinationsAfterOutStation = getCombinations(swapPartner.getOutStation(), penultimateStation, inStationsWithFreeCapacity, passengersOfInStation);
            if (combinationsAfterOutStation.size() == 0) {
                return new ArrayList<>();
            }
            List<Passenger> passengers = combinationsAfterOutStation.get(random.nextInt(combinationsAfterOutStation.size()));
            swapPartners.addAll(passengers);
        }
        swapPartners.removeAll(Collections.singleton(null));
        return swapPartners;
    }

    private static List<List<Passenger>> getCombinations(int inStation, int penultimateStation,
                                                         List<Integer> inStationsWithFreeCapacity, Map<Integer, List<Passenger>> passengersOfInStation) {
        for (int stationId = inStation; stationId <= penultimateStation; stationId++) {
            if (!passengersOfInStation.containsKey(stationId)) {
                passengersOfInStation.put(stationId, new ArrayList<>());
            }
        }

        List<List<Passenger>> combinations = new ArrayList<>();
        for (int stationId = inStation; stationId <= penultimateStation; stationId++) {
            if (combinations.size() > 0) {
                List<List<Passenger>> newCombinations = new ArrayList<>();
                for (List<Passenger> combination : combinations) {
                    if (combination.get(combination.size() - 1) == null || combination.get(combination.size() - 1).getOutStation() == stationId) {
                        if (inStationsWithFreeCapacity.contains(stationId)) {
                            addFreeSeat(newCombinations, combination);
                        }
                        List<Passenger> laterPassengers = passengersOfInStation.get(stationId);
                        for (Passenger p : laterPassengers) {
                            addPassenger(new ArrayList<>(combination), p, newCombinations);
                        }
                    }
                }
                combinations = newCombinations;
            } else {
                if (inStationsWithFreeCapacity.contains(stationId)) {
                    addFreeSeat(combinations, new ArrayList<>());
                }
                List<Passenger> laterPassengers = passengersOfInStation.get(stationId);
                for (Passenger p : laterPassengers) {
                    addPassenger(new ArrayList<>(), p, combinations);
                }
            }
        }
        return combinations;
    }

    private static void addPassenger(ArrayList<Passenger> combination, Passenger p, List<List<Passenger>> combinations) {
        combination.add(p);
        combinations.add(combination);
    }

    private static void addFreeSeat(List<List<Passenger>> combinations, List<Passenger> combination) {
        addPassenger(new ArrayList<>(combination), null, combinations);
    }

    private static List<Integer> getInStationsWithFreeCapacity(Individual individual, int inStation,
                                                               int penultimateStation, int railCarriageId) {

        return individual.getPassengersOfRailCarriageOfInStation().entrySet().stream()
            .filter(entry -> entry.getKey() >= inStation && entry.getKey() <= penultimateStation)
            .filter(entry -> entry.getValue().get(railCarriageId).hasFreeCapacity())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

}
