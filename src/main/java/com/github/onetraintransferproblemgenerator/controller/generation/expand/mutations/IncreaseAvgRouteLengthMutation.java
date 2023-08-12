package com.github.onetraintransferproblemgenerator.controller.generation.expand.mutations;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.LocalSearchIndividual;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.List;
import java.util.Random;

public class IncreaseAvgRouteLengthMutation extends PassengerMutation {

    @Override
    public LocalSearchIndividual mutate(LocalSearchIndividual individual) {
        List<Tuple<Integer, Integer>> availableRides =
            individual.getStationCapacityTracker().getAvailableRides();

        if (availableRides.size() == 0)
            return individual;

        Random random = new Random();
        Tuple<Integer, Integer> availableCapacity = availableRides.get(random.nextInt(availableRides.size()));
        int startStation = availableCapacity.getLeft();
        int endStation = availableCapacity.getRight();

        List<Passenger> potentialPassengers = individual.getProblemInstance().getProblem().getPassengers().stream()
            .filter(passenger -> passenger.getOutStation() < endStation && passenger.getOutStation() >= startStation)
            .toList();

        if (potentialPassengers.size() == 0)
            return individual;

        Passenger passengerToExtend = potentialPassengers.get(random.nextInt(potentialPassengers.size()));

        passengerToExtend.setOutStation(endStation);
        return individual;
    }


}
