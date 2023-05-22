package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Train {
    private ArrayList<RailCarriage> railCarriages = new ArrayList<>();
    /**
     * left: station id, right: platform position
     */
    private ArrayList<Tuple<Integer, StationOperation>> stations = new ArrayList<>();

    public int getTotalCapacity() {
        return railCarriages.stream().map(RailCarriage::getCapacity).reduce(0, Integer::sum);
    }

    public List<Integer> getStationIds() {
        return stations.stream().map(Tuple::getLeft).toList();
    }

    public int getTrainLength() {
        return railCarriages.size();
    }

    public int getDistanceBetweenPositionAndCarriagePosition(int stationId, int carriageId, int position) {
        return Math.abs(getCarriagePosition(stationId, carriageId) - position);
    }

    public int getCarriagePosition(int stationId, int carriageId) {
        StationOperation stationOperation = stations.stream()
            .filter(t -> t.getLeft().equals(stationId))
            .findFirst()
            .get()
            .getRight();

        if (stationOperation.getTravelDirection().equals(DirectionOfTravel.ascending)) {
            return stationOperation.getPosition() + carriageId - 1;
        } else {
            return stationOperation.getPosition() + getTrainLength() - carriageId;
        }
    }
}
