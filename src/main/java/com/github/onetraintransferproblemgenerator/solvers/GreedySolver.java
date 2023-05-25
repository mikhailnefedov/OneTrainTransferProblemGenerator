package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GreedySolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;

    public GreedySolver(OneTrainTransferProblem problem) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
    }

    @Override
    public double solve() {

        for (Passenger passenger : problem.getPassengers()) {
            Map<Integer, Integer> distancesOfRailCarriage = getDistanceIfRailCarriageIsUsed(passenger);
            // TODO: sort min -> max, try in loop if available capacity
        }

        return 0;
    }

    private Map<Integer, Integer> getDistanceIfRailCarriageIsUsed(Passenger passenger) {
        HashMap<Integer, Integer> inDistances =
            carriagePositionHelper.getDistancesBetweenRailCarriagesAndPosition(passenger.getInStation(), passenger.getInPosition());
        HashMap<Integer, Integer> outDistances =
            carriagePositionHelper.getDistancesBetweenRailCarriagesAndPosition(passenger.getOutStation(), passenger.getOutPosition());

        Map<Integer, Integer> combinedDistances = Stream.of(inDistances, outDistances)
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                Integer::sum));
        return combinedDistances;
    }
}
