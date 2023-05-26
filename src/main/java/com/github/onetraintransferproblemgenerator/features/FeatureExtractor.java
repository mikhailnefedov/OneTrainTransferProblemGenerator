package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.models.DirectionOfTravel;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;

import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor {

    public static InstanceFeatureDescription extract(String instanceId, OneTrainTransferProblem problem) {
        InstanceFeatureDescription description = new InstanceFeatureDescription();
        description.setInstanceId(instanceId);
        description.setStationCount(getStationCount(problem));
        description.setPassengerCount(getPassengerCount(problem));
        description.setDirectionChangeCount(getDirectionChangeCount(problem));
        description.setAverageRailCarriageCapacity(getAverageRailCarriageCapacity(problem));
        setCongestion(description, problem);
        return description;
    }

    private static int getDirectionChangeCount(OneTrainTransferProblem problem) {
        DirectionOfTravel tmpDirection = null;
        int directionChangeCount = 0;
        for (DirectionOfTravel travelDirection : problem.getTrain().getStations().stream()
            .map(t -> t.getStationOperation().getTravelDirection()).toList()) {
            if (tmpDirection == null) {
                tmpDirection = travelDirection;
            } else if (!travelDirection.equals(tmpDirection)) {
                tmpDirection = travelDirection;
                directionChangeCount += 1;
            }
        }
        return directionChangeCount;
    }

    private static int getPassengerCount(OneTrainTransferProblem problem) {
        return problem.getPassengers().size();
    }

    private static int getStationCount(OneTrainTransferProblem problem) {
        return problem.getTrain().getStations().size();
    }

    private static double getAverageRailCarriageCapacity(OneTrainTransferProblem problem) {
        return problem.getTrain().getRailCarriages().stream()
            .map(RailCarriage::getCapacity)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }

    private static void setCongestion(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        List<Double> congestions = getCongestionsOfSubRoutes(problem);
        description.setMaxCongestion(getMaxCongestion(congestions));
        description.setAverageCongestion(getAverageCongestion(congestions));
    }

    private static double getAverageCongestion(List<Double> congestions) {
        return congestions.stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    //TODO: Refactor method to call both avg and max and then setting it
    private static double getMaxCongestion(List<Double> congestions) {
        return congestions.stream().max(Double::compare).orElse(0.0);
    }

    private static List<Double> getCongestionsOfSubRoutes(OneTrainTransferProblem problem) {
        double trainCapacity = problem.getTrain().getTotalCapacity();
        double currentPassengerCount = 0;
        List<Double> congestions = new ArrayList<>();

        for (Integer stationId : problem.getTrain().getStationIds()) {
            congestions.add(currentPassengerCount / trainCapacity);
            currentPassengerCount += problem.getInPassengersOfStation(stationId).size();
            currentPassengerCount -= problem.getOutPassengersOfStation(stationId).size();
        }
        return congestions;
    }
}
