package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplitCrossover {

    public static ConflictEvolutionIndividual doCrossover(ConflictEvolutionIndividual parent1, ConflictEvolutionIndividual parent2) {
        int stations = parent1.getProblemInstance().getProblem().getTrain().getStations().size();
        Random random = new Random();

        int crossoverPoint = random.nextInt(1, stations + 1);

        List<Passenger> newPassengerList = new ArrayList<>(parent1.getProblemInstance().getProblem().getPassengers().stream()
            .filter(p -> p.getInStation() < crossoverPoint)
            .map(Passenger::deepClone)
            .toList());

        newPassengerList.addAll(parent2.getProblemInstance().getProblem().getPassengers().stream()
            .filter(p -> p.getInStation() >= crossoverPoint)
            .map(Passenger::deepClone)
            .toList());

        ConflictEvolutionIndividual child = parent1.deepClone();
        child.getProblemInstance().getProblem().setPassengers(newPassengerList);
        return child;
    }
}
