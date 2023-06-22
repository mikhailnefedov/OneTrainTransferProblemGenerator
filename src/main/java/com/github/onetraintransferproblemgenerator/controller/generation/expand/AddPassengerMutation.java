package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;

import java.util.List;
import java.util.Random;

class AddPassengerMutation implements Mutation {

    private int passengerId = 1000000;

    @Override
    public ExpandInstanceIndividual mutate(ExpandInstanceIndividual individual) {
        List<Tuple<Integer, Integer>> availableRides =
                individual.getStationCapacityTracker().getAvailableRides();
        if (availableRides.size() == 0) {
            return individual;
        } else {
            Tuple<Integer, Integer> newRide = chooseRandomRide(availableRides);
            Passenger passenger = new Passenger();
            passenger.setId(passengerId++);
            passenger.setInStation(newRide.getLeft());
            passenger.setInPosition(1);
            passenger.setOutStation(newRide.getRight());
            passenger.setOutPosition(1);
            individual.addPassenger(passenger);
        }
        return individual;
    }

    private Tuple<Integer, Integer> chooseRandomRide(List<Tuple<Integer, Integer>> availableRides) {
        Random random = new Random();
        Tuple<Integer, Integer> randomRide = availableRides.get(random.nextInt(availableRides.size()));

        int startStation = randomRide.getLeft();
        int lastStation = randomRide.getRight();
        int randomLastStation = random.nextInt(startStation,lastStation + 1);
        return new Tuple<>(startStation, randomLastStation);
    }
}
