package com.github.onetraintransferproblemgenerator.generation.realistic;

import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.models.*;

import java.util.*;
import java.util.stream.Collectors;

public class RealisticGenerator implements OneTrainTransferProblemGenerator {

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

    private final int maxStationCount = 24;
    private Random random;

    public RealisticGenerator() {
        stations = RealisticDataReader.getStations();
        trains = RealisticDataReader.getTrains();

        random = new Random();
    }

    @Override
    public OneTrainTransferProblem generate() {
        Train train = generateTrain();
        List<Station> trainRoute = generateStations(train);

        //TODO: Write Converter to Model, abide by logical rules
        convertTrainToModel(train, trainRoute);
        //TODO: Generate Passengers, try different capacity usages
        return null;
    }

    private Train generateTrain() {
        return trains.get(random.nextInt(trains.size()));
    }

    private List<Station> generateStations(Train train) {
        int stationCount = random.nextInt(2, maxStationCount + 1);
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

    private List<StationTuple> convertTrainToModel(Train train, List<Station> trainRoute) {
        com.github.onetraintransferproblemgenerator.models.Train trainModel = new com.github.onetraintransferproblemgenerator.models.Train();
        trainModel.setRailCarriages(train.getCarriageData().stream()
                .map(rC -> new RailCarriage(rC.getCarriageId(), rC.getCapacity()))
                .collect(Collectors.toList())
        );

        List<StationTuple> stationTuples = convertStations(train, trainRoute);
        return setDirectionOfTravel(stationTuples, trainRoute);
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
        return stationTuples;
    }

    private StationTuple convertRailheadStation(int stationId) {
        StationOperation stationOperation = new StationOperation(1, 1, DirectionOfTravel.ascending);
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

        StationOperation stationOperation = new StationOperation(1, trainPosition, DirectionOfTravel.ascending);
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

    public static void main(String[] args) {
        RealisticGenerator generator = new RealisticGenerator();
        generator.generate();
    }
}
