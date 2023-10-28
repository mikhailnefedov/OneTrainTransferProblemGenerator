package com.github.onetraintransferproblemgenerator.controller.generation.expand.mutations;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.LocalSearchIndividual;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.List;
import java.util.Random;

public class RemovePassengerMutation extends PassengerMutation {
    @Override
    public LocalSearchIndividual mutate(LocalSearchIndividual individual) {

        List<Passenger> passengers = individual.getProblemInstance().getProblem().getPassengers();
        Random random = new Random();

        if (passengers.size() > 0) {
            Passenger passengerToRemove = passengers.get(random.nextInt(passengers.size()));

            individual.removePassenger(passengerToRemove);
        }

        return individual;
    }
}
