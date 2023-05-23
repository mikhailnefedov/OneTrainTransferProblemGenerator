package com.github.onetraintransferproblemgenerator.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class OneTrainTransferProblem {
    private Train train;
    private List<Passenger> passengers;

    public List<Passenger> getInPassengersOfStation(int stationId) {
        return passengers.stream().filter(p -> p.getInStation() == stationId).toList();
    }

    public List<Passenger> getOutPassengersOfStation(int stationId) {
        return passengers.stream().filter(p -> p.getOutStation() == stationId).toList();
    }
}
