package com.github.onetraintransferproblemgenerator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneTrainTransferProblem {
    private Train train;
    private List<Passenger> passengers;

    public List<Passenger> getInPassengersOfStation(int stationId) {
        return passengers.stream().filter(p -> p.getInStation() == stationId).toList();
    }

    public List<Passenger> getOutPassengersOfStation(int stationId) {
        return passengers.stream().filter(p -> p.getOutStation() == stationId).toList();
    }

    public OneTrainTransferProblem deepCopy() {
        OneTrainTransferProblem copy = new OneTrainTransferProblem();
        Train trainCopy = new Train();
        trainCopy.setRailCarriages(train.getRailCarriages().stream().collect(Collectors.toList()));
        trainCopy.setStations(train.getStations().stream().collect(Collectors.toList()));
        copy.setTrain(trainCopy);
        copy.setPassengers(passengers.stream().collect(Collectors.toList()));
        return copy;
    }
}
