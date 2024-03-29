package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.StationTuple;
import com.github.onetraintransferproblemgenerator.models.Train;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RailCarriagePositionHelper {

    private HashMap<Integer, HashMap<Integer, Integer>> railCarriagePositionsOfStations;
    private final Train train;
    private final int lastRailCarriageId;

    public RailCarriagePositionHelper(Train train) {
        this.train = train;
        lastRailCarriageId = train.getRailCarriages().size();
        railCarriagePositionsOfStations = new HashMap<>();

        for (StationTuple station : train.getStations()) {
            setRailCarriagePositionsOfStation(station.getStationId());
        }
    }

    private void setRailCarriagePositionsOfStation(int stationId) {
        railCarriagePositionsOfStations.put(stationId, new HashMap<>());

        int firstPosition = train.getCarriagePosition(stationId, 1);
        int lastPosition = train.getCarriagePosition(stationId, lastRailCarriageId);

        if (lastPosition > firstPosition) {
            int position = firstPosition;
            for (int i = 1; i <= lastRailCarriageId; i++) {
                railCarriagePositionsOfStations.get(stationId).put(i, position);
                position++;
            }
        } else if (lastPosition < firstPosition) {
            int position = firstPosition;
            for (int i = 1; i <= lastRailCarriageId; i++) {
                railCarriagePositionsOfStations.get(stationId).put(i, position);
                position--;
            }
        } else {
            railCarriagePositionsOfStations.get(stationId).put(1, firstPosition);
        }
    }

    public List<RailCarriageDistance> getDistancesForRailCarriages(Passenger passenger) {
        HashMap<Integer, Integer> inDistances =
                getDistancesBetweenRailCarriagesAndPosition(passenger.getInStation(), passenger.getInPosition());
        HashMap<Integer, Integer> outDistances =
                getDistancesBetweenRailCarriagesAndPosition(passenger.getOutStation(), passenger.getOutPosition());

        return inDistances.entrySet().stream().map(entry -> {
            int railCarriageId = entry.getKey();
            int inDistance = entry.getValue();
            int outDistance = outDistances.get(railCarriageId);
            return new RailCarriageDistance(railCarriageId, inDistance, outDistance);
        }).collect(Collectors.toList());
    }

    public RailCarriageDistance getDistanceOfRailCarriage(Passenger passenger, int railCarriageId) {
        int inDistance = Math.abs(
                railCarriagePositionsOfStations.get(passenger.getInStation()).get(railCarriageId) - passenger.getInPosition());
        int outDistance = Math.abs(
                railCarriagePositionsOfStations.get(passenger.getOutStation()).get(railCarriageId) - passenger.getOutPosition());

        return new RailCarriageDistance(railCarriageId, inDistance, outDistance);
    }

    private HashMap<Integer, Integer> getDistancesBetweenRailCarriagesAndPosition(int stationId, int position) {
        HashMap<Integer, Integer> resultMap = new HashMap<>();
        HashMap<Integer, Integer> railCarriagePositions = railCarriagePositionsOfStations.get(stationId);

        railCarriagePositions.entrySet().forEach(entry -> {
            int carriagePosition = entry.getValue();
            resultMap.put(entry.getKey(), Math.abs(carriagePosition - position));
        });
        return resultMap;
    }
}
