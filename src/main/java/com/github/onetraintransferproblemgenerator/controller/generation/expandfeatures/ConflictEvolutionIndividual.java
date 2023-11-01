package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.helpers.MathUtils;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class ConflictEvolutionIndividual {

    private Map<Integer, Integer> maxPositionOfStation;
    private ProblemInstance problemInstance;
    private OriginalCoordinate originalCoordinate;
    private double fitness;
    private boolean possibleToCreateConflicts = false;

    public ConflictEvolutionIndividual deepClone() {
        ConflictEvolutionIndividual copyInd = new ConflictEvolutionIndividual();
        copyInd.setProblemInstance(problemInstance.deepClone());
        copyInd.setMaxPositionOfStation(maxPositionOfStation);
        copyInd.setOriginalCoordinate(originalCoordinate);
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

    /**
    public void setOriginalCoordinate(double blockedPassengerRatio, double conflictFreePassengerSeatingRatio) {
        originalCoordinate.setBlockedPassengerRatio(blockedPassengerRatio);
        originalCoordinate.setConflictFreePassengerSeatingRatio(conflictFreePassengerSeatingRatio);
    }
     */

    public double computeAndSetFitness(ConflictCoordinate targetPoint) {
        double blockedPassengerRatio = problemInstance.getFeatureDescription().getBlockedPassengerRatio();
        double conflictFreePassengerSeatingRatio = problemInstance.getFeatureDescription().getConflictFreePassengerSeatingRatio();

        double distanceToOrigin =
            MathUtils.computeDistance(originalCoordinate.getBlockedPassengerRatio(), originalCoordinate.getConflictFreePassengerSeatingRatio(), blockedPassengerRatio, conflictFreePassengerSeatingRatio);
        double distanceToTargetPoint =
            MathUtils.computeDistance(targetPoint.getBlockedPassengerRatio(), targetPoint.getConflictFreePassengerSeatingRatio(), blockedPassengerRatio, conflictFreePassengerSeatingRatio);

        double maxDistanceOriginTarget =
            MathUtils.computeDistance(targetPoint.getBlockedPassengerRatio(), targetPoint.getConflictFreePassengerSeatingRatio(), originalCoordinate.getBlockedPassengerRatio(), originalCoordinate.getConflictFreePassengerSeatingRatio());


        double penalty = 0;
        if (distanceToOrigin > maxDistanceOriginTarget) {
            penalty = 42;   //Infinity penalty, is okay here because instances are all in square 1.0 x 1.0
        }

        double maxOriginRatio = -0.5;
        double minTargetRatio = 0.5;

        fitness = maxOriginRatio * distanceToOrigin + minTargetRatio * distanceToTargetPoint + penalty;

        return fitness;
    }

    public void computePossibleToCreateConflicts() {
        int minCarriageSize = problemInstance.getProblem().getTrain()
            .getRailCarriages().stream()
            .min(Comparator.comparingInt(RailCarriage::getCapacity))
            .get()
            .getCapacity();

        List<Integer> stationIds = problemInstance.getProblem().getTrain().getStationIds();

        for (Integer stationId : stationIds) {
            minCarriageSize += problemInstance.getProblem().getOutPassengersOfStation(stationId).size();
            minCarriageSize -= problemInstance.getProblem().getInPassengersOfStation(stationId).size();

            if (minCarriageSize < 0) {
                possibleToCreateConflicts = true;
            }
        }
    }
}
