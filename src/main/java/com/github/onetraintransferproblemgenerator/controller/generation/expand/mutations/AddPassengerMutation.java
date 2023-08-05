package com.github.onetraintransferproblemgenerator.controller.generation.expand.mutations;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.LocalSearchIndividual;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.StationTuple;

import java.util.List;
import java.util.Random;

public class AddPassengerMutation extends PassengerMutation {

    @Override
    public LocalSearchIndividual mutate(LocalSearchIndividual individual) {
        int newPassengerId = getNextPassengerId(individual.getProblemInstance());
        int railCarriageCount = individual.getProblemInstance().getProblem().getTrain().getRailCarriages().size();
        List<StationTuple> stations = individual.getProblemInstance().getProblem().getTrain().getStations();
        Random random = new Random();

        List<Tuple<Integer, Integer>> availableRides =
            individual.getStationCapacityTracker().getAvailableRides();
        if (availableRides.size() == 0) {
            return individual;
        } else {
            Tuple<Integer, Integer> newRide = chooseRandomRide(availableRides);
            Passenger passenger = new Passenger();
            passenger.setId(newPassengerId);
            passenger.setInStation(newRide.getLeft());
            passenger.setOutStation(newRide.getRight());

            int inStationTrainPosition = stations.get(passenger.getInStation() - 1).getStationOperation().getPosition();
            int inPosition = random.nextInt(inStationTrainPosition, inStationTrainPosition + railCarriageCount);
            passenger.setInPosition(inPosition);

            int outStationTrainPosition = stations.get(passenger.getOutStation() - 1).getStationOperation().getPosition();
            int outPosition = random.nextInt(outStationTrainPosition, outStationTrainPosition + railCarriageCount);
            passenger.setOutPosition(outPosition);

            individual.addPassenger(passenger);
        }
        return individual;
    }

    private Tuple<Integer, Integer> chooseRandomRide(List<Tuple<Integer, Integer>> availableRides) {
        Random random = new Random();
        Tuple<Integer, Integer> randomRide = availableRides.get(random.nextInt(availableRides.size()));

        int startStation = randomRide.getLeft();
        int lastStation = randomRide.getRight();
        int randomLastStation = random.nextInt(startStation, lastStation + 1);
        return new Tuple<>(startStation, randomLastStation);
    }
}
