package com.github.onetraintransferproblemgenerator.generation;

import com.github.onetraintransferproblemgenerator.models.*;

import java.util.ArrayList;

public class SimpleGenerator {

    public static void main(String[] args) {
        generateSimpleScenario();
    }

    private static void generateSimpleScenario() {
        int stationCount = 5;
        int positionCount = 10;
        int railCarriageCount = 3;
        int railCarriageMaxCapacity = 10;

        ArrayList<Station> stations = new ArrayList<>();
        for (int i = 1; i <= stationCount; i++) {
            Station station = new Station();
            station.setId(i);
            station.getPlatforms().add(new Platform(1, positionCount));
            stations.add(station);
        }

        Train train = new Train();
        for (int i = 1; i <= railCarriageCount; i++) {
            RailCarriage railCarriage = new RailCarriage();
            railCarriage.setSequenceNumber(i);
            railCarriage.setCapacity(railCarriageMaxCapacity);
            train.getRailCarriages().add(railCarriage);
        }

        for (Station station : stations) {
            Tuple<Integer, StationOperation> tuple = new Tuple<>();
            tuple.setLeft(station.getId());

            StationOperation stationOperation = new StationOperation();
            stationOperation.setPlatformId(1);
            stationOperation.setPosition(1);
            stationOperation.setTravelDirection(DirectionOfTravel.ascending);

            tuple.setRight(stationOperation);
            train.getStations().add(tuple);
        }

        System.out.println(train.toString());

    }

}
