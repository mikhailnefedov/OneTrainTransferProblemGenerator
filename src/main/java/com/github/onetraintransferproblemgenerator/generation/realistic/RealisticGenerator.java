package com.github.onetraintransferproblemgenerator.generation.realistic;

import com.github.onetraintransferproblemgenerator.generation.BaseGenerator;
import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.models.*;

import java.util.*;
import java.util.stream.Collectors;

public class RealisticGenerator extends BaseGenerator {

    private List<Station> stations;
    private List<Train> trains;
    /**
     * Share of station classes along a train route (Sum must be 1.0 and value can only have 2 decimal values)
     */
    private final Map<Integer, Double> stationClassShare = new HashMap<>(
            Map.of( 1, 0.4,
                    2, 0.4,
                    3, 0.2)
    );

    private Random random;
    private Map<Integer, Integer> freeCapacityFromStation;

    public RealisticGenerator() {
        stations = RealisticDataReader.getStations();
        trains = RealisticDataReader.getTrains();
        List<Integer> capacities = trains.stream().map(x -> x.getCarriageData().stream().map(Train.CarriageData::getCapacity).reduce(Integer::sum).get()).toList();

        random = new Random();
    }

    @Override
    public OneTrainTransferProblem generate() {
        Train train = generateTrain();
        List<Station> trainRoute = generateStations(train);

        com.github.onetraintransferproblemgenerator.models.Train trainModel = convertTrainToModel(train, trainRoute);
        List<Passenger> passengers = generatePassengers(trainRoute, trainModel);

        return new OneTrainTransferProblem(trainModel, passengers);
    }

    private Train generateTrain() {
        return trains.get(random.nextInt(trains.size()));
    }

    private List<Station> generateStations(Train train) {
        int stationCount = random.nextInt(MIN_STATION_COUNT, MAX_STATION_COUNT + 1);
        Map<Integer, List<Station>> appropriateStations = getAppropriateStationsForTrain(train.getCarriageCount());
        List<Integer> stationClassProbabilityList = generateProbabilityList();

        List<Station> trainRoute = new ArrayList<>();
        for (int i = 0; i < stationCount; i++) {
            int stationClass = stationClassProbabilityList.get(random.nextInt(stationClassProbabilityList.size()));
            List<Station> stationsOfClass = appropriateStations.get(stationClass);
            Station station = stationsOfClass.get(random.nextInt(stationsOfClass.size()));
            trainRoute.add(station);
        }
        return trainRoute;
    }

    private Map<Integer, List<Station>> getAppropriateStationsForTrain(int carriageCount) {
        return stations.stream()
                .filter(station -> station.getNumberOfPositions() >= carriageCount)
                .collect(Collectors.groupingBy(Station::getStationClass));
    }

    private List<Integer> generateProbabilityList() {
        List<Integer> stationClassProbabilityList = new ArrayList<>();
        int PROBABILITY_TOTAL = 100;
        for (Integer key : stationClassShare.keySet()) {
            int keyCount = (int) (stationClassShare.get(key) * PROBABILITY_TOTAL);
            stationClassProbabilityList.addAll(new ArrayList<>(Collections.nCopies(keyCount, key)));
        }
        return stationClassProbabilityList;
    }

    private com.github.onetraintransferproblemgenerator.models.Train convertTrainToModel(Train train, List<Station> trainRoute) {
        com.github.onetraintransferproblemgenerator.models.Train trainModel = new com.github.onetraintransferproblemgenerator.models.Train();
        trainModel.setRailCarriages(train.getCarriageData().stream()
                .map(rC -> new RailCarriage(rC.getCarriageId(), rC.getCapacity()))
                .collect(Collectors.toList())
        );

        trainModel.setStations(convertStations(train, trainRoute));
        return trainModel;
    }

    private List<StationTuple> convertStations(Train train, List<Station> trainRoute) {
        List<StationTuple> stationTuples = new ArrayList<>();
        for (int i = 0; i < trainRoute.size(); i++) {
            Station station = trainRoute.get(i);
            if (station.isRailhead()) {
                stationTuples.add(convertRailheadStation(i + 1));
            } else {
                stationTuples.add(convertSimpleStation(station, i + 1, train));
            }
        }
        return setDirectionOfTravel(stationTuples, trainRoute);
    }

    private StationTuple convertRailheadStation(int stationId) {
        StationOperation stationOperation = new StationOperation(1, DirectionOfTravel.ascending);
        return new StationTuple(stationId, stationOperation);
    }

    private StationTuple convertSimpleStation(Station station, int stationId, Train train) {
        int positionCount = station.getNumberOfPositions();
        int carriageCount = train.getCarriageCount();

        int freePositions = positionCount - carriageCount;

        int trainPosition = 1;
        if (freePositions > 0) {
            trainPosition = random.nextInt(1, freePositions + 2);
        }

        StationOperation stationOperation = new StationOperation(trainPosition, DirectionOfTravel.ascending);
        return new StationTuple(stationId, stationOperation);
    }

    private List<StationTuple> setDirectionOfTravel(List<StationTuple> stationTuples, List<Station> originalStations) {
        DirectionOfTravel lastDirectionOfTravel = DirectionOfTravel.ascending;
        for (int i = 1; i < originalStations.size(); i++) {
            if (originalStations.get(i).isRailhead()) {
                lastDirectionOfTravel =
                    lastDirectionOfTravel.equals(DirectionOfTravel.ascending) ? DirectionOfTravel.descending : DirectionOfTravel.ascending;
            }
            stationTuples.get(i).getStationOperation().setTravelDirection(lastDirectionOfTravel);
        }
        return stationTuples;
    }

    private List<Passenger> generatePassengers(List<Station> trainRoute, com.github.onetraintransferproblemgenerator.models.Train trainModel) {
        initializeCapacityTracker(trainModel);
        int availableRouteSectionCount = computeAvailableRouteSections(trainModel);

        List<Passenger> passengers = new ArrayList<>();
        int passengerId = 1;
        while (availableRouteSectionCount > 0) {
            List<Integer> possibleStartStations = getAllPossibleStartStations();
            int startStation = possibleStartStations.get(random.nextInt(possibleStartStations.size()));
            int lastPossiblePenultimateStation = getPenultimateStation(startStation, trainModel);
            int lastStation = random.nextInt(startStation + 1, lastPossiblePenultimateStation + 2);
            updateFreeCapacitiesFromStation(startStation, lastStation - 1);

            Passenger p = new Passenger();
            p.setId(passengerId);
            p.setInStation(startStation);
            p.setOutStation(lastStation);
            p.setInPosition(getInPosition(stations, startStation));
            p.setOutPosition(getOutPosition(stations, lastStation));
            passengers.add(p);

            passengerId++;
            availableRouteSectionCount-= lastStation - startStation;
        }

        if (availableRouteSectionCount < 0) {
            int removeRouteSections = Math.abs(availableRouteSectionCount);
            Passenger passengerWithSmallerRoute = passengers.stream()
                .filter(passenger -> (passenger.getOutStation() - passenger.getInStation()) > removeRouteSections)
                .findFirst()
                .get();

            passengerWithSmallerRoute.setOutStation(passengerWithSmallerRoute.getOutStation() - removeRouteSections);
        }
        return passengers;
    }

    private void initializeCapacityTracker(com.github.onetraintransferproblemgenerator.models.Train train) {
        freeCapacityFromStation = new HashMap<>();

        int totalCapacity = train.getTotalCapacity();
        for (Integer stationId : train.getStationIds()) {
            freeCapacityFromStation.put(stationId, totalCapacity);
        }
        freeCapacityFromStation.remove(train.getStationIds().get(train.getStationCount() - 1));
    }

    private int computeAvailableRouteSections(com.github.onetraintransferproblemgenerator.models.Train trainModel) {
        int totalCapacity = trainModel.getTotalCapacity();
        int stationCount = trainModel.getStationCount();
        
        double randomValue = MIN_CONGESTION + (MAX_CONGESTION - MIN_CONGESTION) * random.nextDouble();
        randomValue = randomValue / CONGESTION_INCREMENT;
        double totalCongestion = Math.floor(randomValue) * CONGESTION_INCREMENT;

        return (int) (totalCapacity * (stationCount - 1) * totalCongestion);
    }

    private List<Integer> getAllPossibleStartStations() {
        return freeCapacityFromStation.entrySet().stream().filter(entry -> entry.getValue() > 0).map(Map.Entry::getKey).toList();
    }

    private int getPenultimateStation(int startStation, com.github.onetraintransferproblemgenerator.models.Train train) {
        int lastPossibleStation = startStation;

        for (int i = startStation + 1; i < train.getStationIds().size(); i++) {
            if (freeCapacityFromStation.get(i) > 0) {
                lastPossibleStation = i;
            } else {
                break;
            }
        }
        return lastPossibleStation;
    }

    private void updateFreeCapacitiesFromStation(int startStation, int penultimateStation) {
        for (int i = startStation; i <= penultimateStation; i++) {
            freeCapacityFromStation.put(i, freeCapacityFromStation.get(i) - 1);
        }
    }

    private int getInPosition(List<Station> stations, int stationId) {
        Station station = stations.get(stationId - 1);
        int positionCount = station.getNumberOfPositions();
        int randomAccessPosition = station.getAccessPositions().get(random.nextInt(station.getAccessPositions().size()));
        if (randomAccessPosition > 2 && randomAccessPosition < positionCount - 2) {
            return random.nextInt(randomAccessPosition - 2, randomAccessPosition + 2);
        }
        return randomAccessPosition;
    }

    private int getOutPosition(List<Station> stations, int stationId) {
        Station station = stations.get(stationId - 1);
        if (station.isRailhead()) {
            return station.getAccessPositions().get(0);
        } else {
            return station.getAccessPositions().get(random.nextInt(station.getAccessPositions().size()));
        }
    }

}
