package com.github.onetraintransferproblemgenerator.generation;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleGenerator {

    private static final int POSITION_COUNT = 10;



    public static OneTrainTransferProblem generateSimpleScenario() {

        int minStationCount = 4;
        int maxStationCount = 6;

        int minRailCarriages = 0;
        int maxRailCarriages = 5;
        int maxRailCarriageCapacity = 10;

        int passengerCount = 6;

        List<Station> stations = generateStations(minStationCount, maxStationCount, POSITION_COUNT);

        Train train = generateTrain(minRailCarriages, maxRailCarriages, maxRailCarriageCapacity);

        train = generateRoute(stations, train);
        List<Passenger> passengers = generatePassengers(stations, passengerCount);

        return new OneTrainTransferProblem(train, passengers);
    }

    private static List<Station> generateStations(int minStationCount, int maxStationCount, int positionCount) {
        ArrayList<Station> stations = new ArrayList<>();
        Random random = new Random();
        int stationCount = random.nextInt(minStationCount, maxStationCount + 1);
        for (int i = 1; i <= stationCount; i++) {
            Station station = new Station();
            station.setId(i);
            station.getPlatforms().add(new Platform(1, positionCount));
            stations.add(station);
        }
        return stations;
    }

    private static Train generateTrain(int minRailCarriages, int maxRailCarriages, int maxCapicity) {
        Train train = new Train();
        Random random = new Random();
        int railCarriageCount = random.nextInt(minRailCarriages, maxRailCarriages + 1);
        int capacity = random.nextInt(maxCapicity);
        for (int i = 1; i <= railCarriageCount; i++) {
            RailCarriage railCarriage = new RailCarriage();
            railCarriage.setSequenceNumber(i);
            railCarriage.setCapacity(capacity);
            train.getRailCarriages().add(railCarriage);
        }
        return train;
    }

    private static Train generateRoute(List<Station> stations, Train train) {
        for (Station station : stations) {
            Tuple<Integer, StationOperation> tuple = new Tuple<>();
            tuple.setLeft(station.getId());

            StationOperation stationOperation = new StationOperation();
            stationOperation.setPlatformId(1);
            stationOperation.setPosition(1);
            DirectionOfTravel travelDirection = DirectionOfTravel.values()[new Random().nextInt(DirectionOfTravel.values().length)];
            stationOperation.setTravelDirection(travelDirection);

            tuple.setRight(stationOperation);
            train.getStations().add(tuple);
        }
        return train;
    }

    private static List<Passenger> generatePassengers(List<Station> stations, int passengerCount) {
        Random random = new Random();
        int stationCount = stations.size();
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < passengerCount; i++) {
            Passenger passenger = new Passenger();

            int startStation = random.nextInt(1, stationCount);
            int endStation = random.nextInt(startStation + 1, stationCount + 1);

            passenger.setInStation(startStation);
            passenger.setOutStation(endStation);

            passenger.setInPosition(random.nextInt(POSITION_COUNT));
            passenger.setTargetPosition(random.nextInt(POSITION_COUNT));

            passengers.add(passenger);
        }

        return passengers;
    }

}
