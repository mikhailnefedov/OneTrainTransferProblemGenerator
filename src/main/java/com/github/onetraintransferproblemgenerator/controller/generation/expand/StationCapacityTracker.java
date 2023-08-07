package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StationCapacityTracker {

    private HashMap<Integer, Integer> freeCapacityFromStation;

    public StationCapacityTracker(OneTrainTransferProblem problem) {
        initializeStations(problem.getTrain());
        setFreeCapacity(problem.getPassengers());
    }

    private void initializeStations(Train train) {
        freeCapacityFromStation = new HashMap<>();
        int totalCapacity = train.getTotalCapacity();
        for (int i = 0; i < train.getStationIds().size() - 1; i++) {
            freeCapacityFromStation.put(train.getStationIds().get(i), totalCapacity);
        }
    }

    private void setFreeCapacity(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            addPassenger(passenger);
        }
    }

    public void addPassenger(Passenger passenger) {
        int startStation = passenger.getInStation();
        int penultimateStation = passenger.getOutStation() - 1;
        for (int i = startStation; i <= penultimateStation; i++) {
            int freeCapacity = freeCapacityFromStation.get(i);
            freeCapacityFromStation.put(i, freeCapacity - 1);
        }
    }

    public void removeOneSeat() {
        for (Integer stationId : freeCapacityFromStation.keySet()) {
            int currentCapacity = freeCapacityFromStation.get(stationId);
            freeCapacityFromStation.put(stationId, currentCapacity - 1);
        }
    }

    public void addOneSeat() {
        for (Integer stationId : freeCapacityFromStation.keySet()) {
            int currentCapacity = freeCapacityFromStation.get(stationId);
            freeCapacityFromStation.put(stationId, currentCapacity + 1);
        }
    }

    /**
     * @return tuple containing inStation and outStation
     */
    public List<Tuple<Integer, Integer>> getAvailableRides() {
        List<Tuple<Integer, Integer>> availableRides = new ArrayList<>();

        List<Integer> stationIds = freeCapacityFromStation.keySet().stream().sorted().toList();
        List<Integer> stationIdsWithCapacity = stationIds.stream()
            .filter(stationId -> freeCapacityFromStation.get(stationId) > 0)
            .toList();

        if (stationIdsWithCapacity.size() == 0) {
            return availableRides;
        }
        if (stationIdsWithCapacity.size() == 1) {
            int startStation = stationIdsWithCapacity.get(0);
            availableRides.add(new Tuple<>(startStation, startStation + 1));
            return availableRides;
        }

        int startStation = stationIdsWithCapacity.get(0);
        int penultimateStation = stationIdsWithCapacity.get(0);
        for (int i = 1; i < stationIdsWithCapacity.size(); i++) {
            if (stationIdsWithCapacity.get(i) != penultimateStation + 1) {
                availableRides.add(new Tuple<>(startStation, penultimateStation));
                startStation = stationIdsWithCapacity.get(i);
                penultimateStation = stationIdsWithCapacity.get(i);
            } else {
                penultimateStation += 1;
            }
        }

        if (penultimateStation == stationIds.get(stationIds.size() - 1)) {
            availableRides.add(new Tuple<>(startStation, penultimateStation));
        }

        availableRides.forEach(startPenultimateTuple ->
            startPenultimateTuple.setRight(startPenultimateTuple.getRight() + 1));
        return availableRides;
    }

}
