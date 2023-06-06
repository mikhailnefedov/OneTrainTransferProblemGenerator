package com.github.onetraintransferproblemgenerator.generation;

import com.github.onetraintransferproblemgenerator.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleGenerator implements OneTrainTransferProblemGenerator {

    private final int POSITION_COUNT = 10;
    private Random random;

    public SimpleGenerator() {
        random = new Random();
    }

    @Override
    public OneTrainTransferProblem generate() {
        int minStationCount = 4;
        int maxStationCount = 6;

        int minRailCarriages = 1;
        int maxRailCarriages = 5;
        int maxRailCarriageCapacity = 10;

        List<Station> stations = generateStations(minStationCount, maxStationCount, POSITION_COUNT);

        Train train = generateTrain(minRailCarriages, maxRailCarriages, maxRailCarriageCapacity);

        train = generateRoute(stations, train);
        int passengerCount = train.getTotalCapacity() > 0 ? random.nextInt(train.getTotalCapacity()) : 0;
        List<Passenger> passengers = generatePassengers(stations, passengerCount);

        return new OneTrainTransferProblem(train, passengers);
    }

    private List<Station> generateStations(int minStationCount, int maxStationCount, int positionCount) {
        ArrayList<Station> stations = new ArrayList<>();
        int stationCount = random.nextInt(minStationCount, maxStationCount + 1);
        for (int i = 1; i <= stationCount; i++) {
            Station station = new Station();
            station.setId(i);
            station.getPlatforms().add(new Platform(1, positionCount));
            stations.add(station);
        }
        return stations;
    }

    private Train generateTrain(int minRailCarriages, int maxRailCarriages, int maxCapicity) {
        Train train = new Train();
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

    private Train generateRoute(List<Station> stations, Train train) {
        for (Station station : stations) {
            StationTuple stationTuple = new StationTuple();
            stationTuple.setStationId(station.getId());

            StationOperation stationOperation = new StationOperation();
            stationOperation.setPosition(1);
            DirectionOfTravel travelDirection = DirectionOfTravel.values()[new Random().nextInt(DirectionOfTravel.values().length)];
            stationOperation.setTravelDirection(travelDirection);

            stationTuple.setStationOperation(stationOperation);
            train.getStations().add(stationTuple);
        }
        return train;
    }

    private List<Passenger> generatePassengers(List<Station> stations, int passengerCount) {
        int stationCount = stations.size();
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < passengerCount; i++) {
            Passenger passenger = new Passenger();
            passenger.setId(i);

            int startStation = random.nextInt(1, stationCount);
            int endStation = random.nextInt(startStation + 1, stationCount + 1);

            passenger.setInStation(startStation);
            passenger.setOutStation(endStation);

            passenger.setInPosition(random.nextInt(1, POSITION_COUNT));
            passenger.setOutPosition(random.nextInt(1, POSITION_COUNT));

            passengers.add(passenger);
        }

        return passengers;
    }


}
