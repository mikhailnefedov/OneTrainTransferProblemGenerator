package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.models.DirectionOfTravel;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;

public class FeatureExtractor {

    public static InstanceFeatureDescription extract(String instanceId, OneTrainTransferProblem problem) {
        InstanceFeatureDescription description = new InstanceFeatureDescription();
        description.setInstanceId(instanceId);
        description.setStationCount(getStationCount(problem));
        description.setPassengerCount(getPassengerCount(problem));
        description.setDirectionChangeCount(getDirectionChangeCount(problem));
        description.setMaxTargetPosition(getMaxTargetPosition(problem));
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

    private static int getMaxTargetPosition(OneTrainTransferProblem problem) {
        if (problem.getPassengers().size() == 0) {
            return 0;
        }
        return problem.getPassengers().stream()
            .max((p1, p2) -> Integer.compare(p1.getTargetPosition(), p2.getTargetPosition()))
            .get()
            .getTargetPosition();
    }
}
