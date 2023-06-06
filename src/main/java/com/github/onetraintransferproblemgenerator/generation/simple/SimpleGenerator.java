package com.github.onetraintransferproblemgenerator.generation.simple;

import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleGenerator implements OneTrainTransferProblemGenerator {

    private final int POSITION_COUNT = 10;
    private final int minStationCount = 4;
    private final int maxStationCount = 8;
    private final int minRailCarriages = 1;
    private final int maxRailCarriages = 6;
    private final int maxRailCarriageCapacity = 20;

    private final double minCongestion = 0.05;
    private final double maxCongestion = 0.95;
    private final double congestionIncrement = 0.05;

    private final Random random;

    public SimpleGenerator() {
        random = new Random();
    }

    @Override
    public OneTrainTransferProblem generate() {
        List<Station> stations = generateStations();
        Train train = generateTrain();
        setRouteOfTrain(stations, train);

        List<Passenger> passengers = generatePassengers(train);

        return new OneTrainTransferProblem(train, passengers);
    }

    private List<Station> generateStations() {
        ArrayList<Station> stations = new ArrayList<>();
        int stationCount = random.nextInt(minStationCount, maxStationCount + 1);
        for (int i = 1; i <= stationCount; i++) {
            Station station = new Station();
            station.setId(i);
            station.getPlatforms().add(new Platform(1, POSITION_COUNT));
            stations.add(station);
        }
        return stations;
    }

    private Train generateTrain() {
        Train train = new Train();
        int railCarriageCount = random.nextInt(minRailCarriages, maxRailCarriages + 1);
        int capacity = random.nextInt(maxRailCarriageCapacity);
        for (int i = 1; i <= railCarriageCount; i++) {
            RailCarriage railCarriage = new RailCarriage();
            railCarriage.setSequenceNumber(i);
            railCarriage.setCapacity(capacity);
            train.getRailCarriages().add(railCarriage);
        }
        return train;
    }

    private void setRouteOfTrain(List<Station> stations, Train train) {
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
    }

    private List<Passenger> generatePassengers(Train train) {
        SimplePassengerGenerator passengerGenerator =
                new SimplePassengerGenerator(minCongestion, maxCongestion, congestionIncrement, POSITION_COUNT, train);
        return passengerGenerator.generatePassengers();
    }

}
