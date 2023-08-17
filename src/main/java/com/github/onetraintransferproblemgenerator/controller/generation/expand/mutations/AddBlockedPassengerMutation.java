package com.github.onetraintransferproblemgenerator.controller.generation.expand.mutations;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.LocalSearchIndividual;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriageDistance;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;
import com.github.onetraintransferproblemgenerator.solvers.SeatReservationStorage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class AddBlockedPassengerMutation extends PassengerMutation {

    @Override
    public LocalSearchIndividual mutate(LocalSearchIndividual individual) {

        Map<Integer, List<Integer>> fullRailCarriagesOfStation =
            getFullRailCarriagesOfStation(individual.getProblemInstance().getProblem());
        List<Tuple<Integer, Integer>> availableRides =
            individual.getStationCapacityTracker().getAvailableRides();

        List<Map.Entry<Integer, List<Integer>>> stationEntries = fullRailCarriagesOfStation.entrySet().stream()
            .filter(entry -> availableRides.stream()
                .anyMatch(tuple -> entry.getKey() >= tuple.getLeft() && entry.getKey() < tuple.getRight()))
            .toList();

        if (stationEntries.size() == 0)
            return individual;

        RideInformation newRide = getNewRide(stationEntries, availableRides);
        int newPassengerId = getNextPassengerId(individual.getProblemInstance());
        Passenger newPassenger = createPassenger(newPassengerId, newRide, individual.getProblemInstance().getProblem());
        individual.addPassenger(newPassenger);

        return individual;
    }


    private Map<Integer, List<Integer>> getFullRailCarriagesOfStation(OneTrainTransferProblem problem) {
        RailCarriagePositionHelper railCarriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        SeatReservationStorage capacityStorage = new SeatReservationStorage(problem.getTrain());

        List<Integer> stationIds = problem.getTrain().getStationIds();
        List<Integer> railCarriageIds = problem.getTrain().getRailCarriages().stream().map(RailCarriage::getSequenceNumber).toList();

        Map<Integer, List<Integer>> fullRailCarriagesOfStation = new HashMap<>();

        for (int stationId : stationIds) {
            letPassengersOutOfTrain(capacityStorage, problem.getOutPassengersOfStation(stationId));
            seatPassengersInTrain(capacityStorage, railCarriagePositionHelper, problem.getInPassengersOfStation(stationId));
            List<Integer> fullRailCarriages = getBlockedRailCarriages(capacityStorage, railCarriageIds);
            if (fullRailCarriages.size() > 0) {
                fullRailCarriagesOfStation.put(stationId, fullRailCarriages);
            }
        }

        return fullRailCarriagesOfStation;
    }

    private void letPassengersOutOfTrain(SeatReservationStorage capacityStorage,
                                         List<Passenger> passengers) {
        passengers.forEach(passenger -> {
            try {
                capacityStorage.outPassenger(passenger);
            } catch (NullPointerException ignored) {

            }
        });
    }

    private void seatPassengersInTrain(SeatReservationStorage capacityStorage,
                                       RailCarriagePositionHelper railCarriagePositionHelper,
                                       List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            List<RailCarriageDistance> railCarriageDistances = railCarriagePositionHelper.getDistancesForRailCarriages(passenger);
            railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCost));
            for (RailCarriageDistance railCarriageDistance : railCarriageDistances) {
                if (capacityStorage.isBoardingPossible(railCarriageDistance.getRailCarriageId())) {
                    capacityStorage.inPassenger(railCarriageDistance.getRailCarriageId(), passenger);
                }
                break;
            }
        }
    }

    private List<Integer> getBlockedRailCarriages(SeatReservationStorage capacityStorage,
                                                  List<Integer> railCarriageIds) {
        List<Integer> fullRailCarriages = new ArrayList<>();

        for (Integer railCarriageId : railCarriageIds) {
            if (!capacityStorage.isBoardingPossible(railCarriageId)) {
                fullRailCarriages.add(railCarriageId);
            }
        }
        return fullRailCarriages;
    }

    private RideInformation getNewRide(List<Map.Entry<Integer, List<Integer>>> stationEntries,
                                       List<Tuple<Integer, Integer>> availableRides) {
        Random random = new Random();
        Map.Entry<Integer, List<Integer>> randomEntry = stationEntries.get(random.nextInt(stationEntries.size()));
        int stationId = randomEntry.getKey();
        int railCarriageId = randomEntry.getValue().get(random.nextInt(randomEntry.getValue().size()));

        availableRides = availableRides.stream()
            .filter(tuple -> stationId >= tuple.getLeft() && stationId < tuple.getRight())
            .toList();
        Tuple<Integer, Integer> ride = availableRides.get(random.nextInt(availableRides.size()));
        int endStation = random.nextInt(stationId + 1, ride.getRight() + 1);

        return new RideInformation(stationId, endStation, railCarriageId);
    }

    private Passenger createPassenger(int newPassengerId, RideInformation ride, OneTrainTransferProblem problem) {
        int inPosition = problem.getTrain().getCarriagePosition(ride.getInStation(), ride.getRailCarriageId());
        int outPosition = problem.getTrain().getCarriagePosition(ride.getOutStation(), ride.getRailCarriageId());

        Passenger passenger = new Passenger();
        passenger.setId(newPassengerId);
        passenger.setInStation(ride.getInStation());
        passenger.setInPosition(inPosition);
        passenger.setOutStation(ride.getOutStation());
        passenger.setOutPosition(outPosition);
        return passenger;
    }

    @AllArgsConstructor
    @Data
    private class RideInformation {
        private int inStation;
        private int outStation;
        private int railCarriageId;
    }
}


