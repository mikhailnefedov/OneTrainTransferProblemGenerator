package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.List;
import java.util.Random;

public class MoveInOrOutPositionMutation implements ConflictEvolutionMutation {

    public void mutate(ConflictEvolutionIndividual individual) {
        Random random = new Random();
        List<Passenger> passengers = individual.getProblemInstance().getProblem().getPassengers();

        Passenger passengerToChange = passengers.get(random.nextInt(passengers.size()));

        double randomDouble = random.nextDouble();

        if (randomDouble < 0.5) {
            //Change inPosition
            int inStation = passengerToChange.getInStation();
            int maxPosition = individual.getMaxPositionOfStation().get(inStation);
            int inPosition = passengerToChange.getOutPosition();
            passengerToChange.setOutPosition(movePosition(inPosition, maxPosition, random));
        } else {
            //Change outPosition
            int outStation = passengerToChange.getOutStation();
            int maxPosition = individual.getMaxPositionOfStation().get(outStation);
            int outPosition = passengerToChange.getOutPosition();
            passengerToChange.setOutPosition(movePosition(outPosition, maxPosition, random));
        }

    }

    private static int movePosition(int currentPosition, int maxPosition, Random random) {
        if (currentPosition == 1) {
            return 2;
        } else if (currentPosition >= maxPosition - 1) {
            return currentPosition - 1;
        } else {
            double randomDouble = random.nextDouble();
            return randomDouble < 0.5 ? currentPosition + 1: currentPosition - 1;
        }
    }
}
