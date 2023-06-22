package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * @return tuple containing inStation and outStation
     */
    public List<Tuple<Integer, Integer>> getAvailableRides() {
        List<Tuple<Integer, Integer>> availableRides = new ArrayList<>();

        List<Integer> stationIds = freeCapacityFromStation.keySet().stream().toList();

        int startStation = 0;
        int penultimateStation = 0;
        boolean foundStart = false;
        for (Integer stationId : stationIds) {
            if (isPossibleStartStation(stationId, foundStart)) {
                foundStart = true;
                startStation = stationId;
            } else if (isOutStation(stationId, foundStart)) {
                foundStart = false;
                availableRides.add(new Tuple<>(startStation, penultimateStation));
            } else if (isTransitStation(stationId, foundStart)) {
                penultimateStation = stationId;
            }
        }

        availableRides.forEach(startPenultimateTuple ->
                startPenultimateTuple.setRight(startPenultimateTuple.getRight() + 1));
        return availableRides;
    }

    private boolean isPossibleStartStation(int stationId, boolean foundStart) {
        return !foundStart && freeCapacityFromStation.get(stationId) > 0;
    }

    private boolean isOutStation(int stationId, boolean foundStart) {
        return foundStart && freeCapacityFromStation.get(stationId) == 0;
    }

    private boolean isTransitStation(int stationId, boolean foundStart) {
        return foundStart && freeCapacityFromStation.get(stationId) > 0;
    }
}
