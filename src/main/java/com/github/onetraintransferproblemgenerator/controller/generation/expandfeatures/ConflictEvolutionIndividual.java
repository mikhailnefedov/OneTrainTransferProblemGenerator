package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.helpers.MathUtils;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Data
public class ConflictEvolutionIndividual {

    private Map<Integer, Integer> maxPositionOfStation;
    private ProblemInstance problemInstance;
    private List<Double> originalCoordinates = new ArrayList<>();
    private double fitness;

    public ConflictEvolutionIndividual deepClone() {
        ConflictEvolutionIndividual copyInd = new ConflictEvolutionIndividual();
        copyInd.setProblemInstance(problemInstance.deepClone());
        copyInd.setMaxPositionOfStation(maxPositionOfStation);
        copyInd.setOriginalCoordinates(new ArrayList<>(originalCoordinates));
        return copyInd;
    }

    public void randomizeInAndOutPositions() {
        List<Passenger> passengers = problemInstance.getProblem().getPassengers();
        Random random = new Random();

        problemInstance.getProblem().setPassengers(passengers.stream().map(p -> {
                Passenger passenger = new Passenger();
                passenger.setId(p.getId());

                passenger.setInStation(p.getInStation());
                int maxPosition = maxPositionOfStation.get(p.getInStation());
                int randomInPosition = random.nextInt(1, maxPosition);
                passenger.setInPosition(randomInPosition);

                passenger.setOutStation(p.getOutStation());
                maxPosition = maxPositionOfStation.get(p.getOutStation());
                int randomOutPosition = random.nextInt(1, maxPosition);
                passenger.setOutPosition(randomOutPosition);

                return passenger;
            }).collect(Collectors.toList())
        );
    }

    public void setOriginalCoordinates(double blockedPassengerRatio, double conflictFreePassengerSeatingRatio) {
        originalCoordinates.add(blockedPassengerRatio);
        originalCoordinates.add(conflictFreePassengerSeatingRatio);
    }

    public double computeAndSetFitness() {
        double blockedPassengerRatio = problemInstance.getFeatureDescription().getBlockedPassengerRatio();
        double conflictFreePassengerSeatingRatio = problemInstance.getFeatureDescription().getConflictFreePassengerSeatingRatio();

        fitness =
            MathUtils.computeDistance(originalCoordinates.get(0), originalCoordinates.get(1), blockedPassengerRatio, conflictFreePassengerSeatingRatio);
        return fitness;
    }
}
