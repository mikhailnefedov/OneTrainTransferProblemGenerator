package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Train {
    private List<RailCarriage> railCarriages = new ArrayList<>();
    /**
     * left: station id, right: platform position
     */
    private List<StationTuple> stations = new ArrayList<>();

    public int getTotalCapacity() {
        return railCarriages.stream().map(RailCarriage::getCapacity).reduce(0, Integer::sum);
    }

    public List<Integer> getStationIds() {
        return stations.stream().map(StationTuple::getStationId).toList();
    }

    public int getTrainLength() {
        return railCarriages.size();
    }

    public int getDistanceBetweenPositionAndCarriagePosition(int stationId, int carriageId, int position) {
        return Math.abs(getCarriagePosition(stationId, carriageId) - position);
    }

    public int getCarriagePosition(int stationId, int carriageId) {
        StationOperation stationOperation = stations.stream()
            .filter(t -> t.getStationId().equals(stationId))
            .findFirst()
            .get()
            .getStationOperation();

        if (stationOperation.getTravelDirection().equals(DirectionOfTravel.ascending)) {
            return stationOperation.getPosition() + carriageId - 1;
        } else {
            return stationOperation.getPosition() + getTrainLength() - carriageId;
        }
    }
}
