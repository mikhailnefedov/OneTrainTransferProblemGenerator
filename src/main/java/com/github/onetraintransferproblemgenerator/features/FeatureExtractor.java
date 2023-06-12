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
        setPassengerFeatures(description, problem);
        description.setDirectionChangeCount(getDirectionChangeCount(problem));
        setRailCarriageCapacities(description, problem);
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

    private static void setPassengerFeatures(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        description.setTotalPassengerCount(getTotalPassengerCount(problem));
        description.setAvgPassengerCount(getAveragePassengerCount(problem));
        description.setAvgPassengerRouteLength(getAveragePassengerRouteLength(problem));
    }

    private static int getTotalPassengerCount(OneTrainTransferProblem problem) {
        return problem.getPassengers().size();
    }

    private static double getAveragePassengerCount(OneTrainTransferProblem problem) {
        return (double) getTotalPassengerCount(problem) / getStationCount(problem);
    }

    private static double getAveragePassengerRouteLength(OneTrainTransferProblem problem) {
        int routeLengthSum = problem.getPassengers().stream()
                .map(p -> p.getOutStation() - p.getInStation())
                .reduce(Integer::sum)
                .orElse(0);
        double avgPassengerRouteLength = (double) routeLengthSum / problem.getPassengers().size();
        return Double.isNaN(avgPassengerRouteLength) ? 0.0 : avgPassengerRouteLength;
    }

    private static int getStationCount(OneTrainTransferProblem problem) {
        return problem.getTrain().getStations().size();
    }

    private static void setRailCarriageCapacities(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        double avgRailCarriageCapacity = getAverageRailCarriageCapacity(problem);
        description.setAvgRailCarriageCapacity(avgRailCarriageCapacity);
        description.setStdDevRailCarriageCapacity(getStandardDeviationRailCarriageCapacity(problem, avgRailCarriageCapacity));
    }

    private static double getAverageRailCarriageCapacity(OneTrainTransferProblem problem) {
        return problem.getTrain().getRailCarriages().stream()
            .map(RailCarriage::getCapacity)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }

    private static double getStandardDeviationRailCarriageCapacity(OneTrainTransferProblem problem, double mean) {
        double variance = problem.getTrain().getRailCarriages().stream()
                .map(rC -> Math.pow(rC.getCapacity() - mean, 2))
                .reduce(Double::sum)
                .orElse(0.0) / problem.getTrain().getRailCarriages().size();
        variance = Double.isNaN(variance) ? 0 : variance;
        return Math.sqrt(variance);
    }

    private static void setCongestion(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        List<Double> congestions = getCongestionsOfSubRoutes(problem);
        description.setAvgCongestion(getAverageCongestion(congestions));
    }

    private static double getAverageCongestion(List<Double> congestions) {
        return congestions.stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    private static List<Double> getCongestionsOfSubRoutes(OneTrainTransferProblem problem) {
        double trainCapacity = problem.getTrain().getTotalCapacity();
        double currentPassengerCount = 0;
        List<Double> congestions = new ArrayList<>();

        for (Integer stationId : problem.getTrain().getStationIds()) {
            double congestion = currentPassengerCount / trainCapacity;
            congestion = Double.isNaN(congestion) ? 0.0 : congestion;
            congestions.add(congestion);
            currentPassengerCount += problem.getInPassengersOfStation(stationId).size();
            currentPassengerCount -= problem.getOutPassengersOfStation(stationId).size();
        }
        return congestions;
    }
}
