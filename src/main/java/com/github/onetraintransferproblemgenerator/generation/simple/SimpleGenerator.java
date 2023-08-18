package com.github.onetraintransferproblemgenerator.generation.simple;

import com.github.onetraintransferproblemgenerator.generation.BaseGenerator;
import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleGenerator extends BaseGenerator {

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
        int stationCount = random.nextInt(MIN_STATION_COUNT, MAX_STATION_COUNT + 1);
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
        int railCarriageCount = random.nextInt(MIN_RAIL_CARRIAGES, MAX_RAIL_CARRIAGES + 1);
        for (int i = 1; i <= railCarriageCount; i++) {
            RailCarriage railCarriage = new RailCarriage();
            railCarriage.setSequenceNumber(i);
            int capacity = random.nextInt(MAX_RAIL_CARRIAGE_CAPACITY);
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
                new SimplePassengerGenerator(MIN_CONGESTION, MAX_CONGESTION, CONGESTION_INCREMENT, POSITION_COUNT, train);
        return passengerGenerator.generatePassengers();
    }

}
