package com.github.onetraintransferproblemgenerator.generation.simple;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.Train;

import java.util.*;

//TODO: Big similarities between this class and passenger generation in .realistic --> better inheritance?/modeling necessary
public class SimplePassengerGenerator {

    /**
     * Congestion will be generated randomly in the defined range
     */
    private final double minCongestion;
    private final double maxCongestion;
    private final double congestionIncrement;
    private final int positionCount;
    private final Train train;
    private Map<Integer, Integer> freeCapacityFromStation;
    private final Random random;

    public SimplePassengerGenerator(double minCongestion, double maxCongestion, double congestionIncrement, int positionCount, Train train) {
        this.minCongestion = minCongestion;
        this.maxCongestion = maxCongestion;
        this.congestionIncrement = congestionIncrement;
        this.positionCount = positionCount;
        this.train = train;
        initializeCapacityTracker();
        random = new Random();
    }

    private void initializeCapacityTracker() {
        freeCapacityFromStation = new HashMap<>();

        int totalCapacity = train.getTotalCapacity();
        for (Integer stationId : train.getStationIds()) {
            freeCapacityFromStation.put(stationId, totalCapacity);
        }
        freeCapacityFromStation.remove(train.getStationIds().get(train.getStationCount() - 1));
    }

    public List<Passenger> generatePassengers() {
        int availableRouteSectionCount = computeAvailableRouteSections();

        List<Passenger> passengers = new ArrayList<>();
        int passengerId = 1;
        while (availableRouteSectionCount > 0) {
            List<Integer> possibleStartStations = getAllPossibleStartStations();
            int startStation = possibleStartStations.get(random.nextInt(possibleStartStations.size()));
            int lastPossiblePenultimateStation = getPenultimateStation(startStation);
            int lastStation = random.nextInt(startStation + 1, lastPossiblePenultimateStation + 2);
            updateFreeCapacitiesFromStation(startStation, lastStation - 1);

            Passenger p = new Passenger();
            p.setId(passengerId);
            p.setInStation(startStation);
            p.setOutStation(lastStation);
            p.setInPosition(generatePosition());
            p.setOutPosition(generatePosition());
            passengers.add(p);

            passengerId++;
            availableRouteSectionCount -= lastStation - startStation;
        }
        return passengers;
    }

    private int computeAvailableRouteSections() {
        int totalCapacity = train.getTotalCapacity();
        int stationCount = train.getStationCount();

        double randomValue = minCongestion + (maxCongestion - minCongestion) * random.nextDouble();
        randomValue = randomValue / congestionIncrement;
        double totalCongestion = Math.floor(randomValue) * congestionIncrement;

        return (int) (totalCapacity * (stationCount - 1) * totalCongestion);
    }

    private List<Integer> getAllPossibleStartStations() {
        return freeCapacityFromStation.entrySet().stream().filter(entry -> entry.getValue() > 0).map(Map.Entry::getKey).toList();
    }

    private int getPenultimateStation(int startStation) {
        int lastPossibleStation = startStation;

        for (int i = startStation + 1; i < train.getStationIds().size(); i++) {
            if (freeCapacityFromStation.get(i) > 0) {
                lastPossibleStation = i;
            } else {
                break;
            }
        }
        return lastPossibleStation;
    }

    private void updateFreeCapacitiesFromStation(int startStation, int penultimateStation) {
        for (int i = startStation; i <= penultimateStation; i++) {
            freeCapacityFromStation.put(i, freeCapacityFromStation.get(i) - 1);
        }
    }

    private int generatePosition() {
        return random.nextInt(1, positionCount);
    }

}
