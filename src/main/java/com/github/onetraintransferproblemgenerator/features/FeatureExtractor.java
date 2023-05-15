package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.models.DirectionOfTravel;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Tuple;

import java.util.stream.Stream;

public class FeatureExtractor {

    public static void extract(OneTrainTransferProblem problem) {
        int stationCount = problem.getTrain().getStations().size();
        int passengerCount = problem.getPassengers().size();

        DirectionOfTravel tmpDirection = null;
        int directionChangeCount = 0;
        for (DirectionOfTravel travelDirection : problem.getTrain().getStations().stream()
            .map(t -> t.getRight().getTravelDirection()).toList()) {
            if (tmpDirection == null) {
                tmpDirection = travelDirection;
            } else if (!travelDirection.equals(tmpDirection) ) {
                tmpDirection = travelDirection;
                directionChangeCount += 1;
            }
        }

        int maxTargetPosition = problem.getPassengers().stream()
            .max((p1, p2) -> Integer.compare(p1.getTargetPosition(), p2.getTargetPosition()))
            .get()
            .getTargetPosition();

        System.out.println(stationCount);
        System.out.println(passengerCount);
        System.out.println(directionChangeCount);
        System.out.println(maxTargetPosition);
    }
}
