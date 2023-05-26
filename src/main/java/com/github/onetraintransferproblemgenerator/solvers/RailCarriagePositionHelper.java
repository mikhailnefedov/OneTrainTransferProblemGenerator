package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.StationTuple;
import com.github.onetraintransferproblemgenerator.models.Train;

import java.util.HashMap;

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

    public HashMap<Integer, Integer> getDistancesBetweenRailCarriagesAndPosition(int stationId, int position) {
        HashMap<Integer, Integer> resultMap = new HashMap<>();
        HashMap<Integer, Integer> railCarriagePositions = railCarriagePositionsOfStations.get(stationId);

        for (Integer railCarriageId : railCarriagePositions.keySet()) {
            int carriagePosition = railCarriagePositions.get(railCarriageId);
            resultMap.put(railCarriageId, Math.abs(carriagePosition - position));
        }
        return resultMap;
    }
}
