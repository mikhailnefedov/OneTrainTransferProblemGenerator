package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.models.DirectionOfTravel;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;

public class FeatureExtractor {

    public static InstanceFeatureDescription extract(String instanceId, OneTrainTransferProblem problem) {
        InstanceFeatureDescription description = new InstanceFeatureDescription();
        description.setInstanceId(instanceId);
        description.setStationCount(getStationCount(problem));
        description.setPassengerCount(getPassengerCount(problem));
        description.setDirectionChangeCount(getDirectionChangeCount(problem));
        description.setAverageRailCarriageCapacity(getAverageRailCarriageCapacity(problem));
        return description;
    }

    private static int getDirectionChangeCount(OneTrainTransferProblem problem) {
        DirectionOfTravel tmpDirection = null;
        int directionChangeCount = 0;
        for (DirectionOfTravel travelDirection : problem.getTrain().getStations().stream()
            .map(t -> t.getRight().getTravelDirection()).toList()) {
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
}
