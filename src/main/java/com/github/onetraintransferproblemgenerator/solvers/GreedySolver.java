package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.HashMap;
import java.util.List;
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
            List<Tuple<Integer, Integer>> distances = distancesOfRailCarriage.entrySet().stream()
                    .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()))
                    .toList();
            for (Tuple<Integer, Integer> distanceOfRailCarriage : distances) {
                if (capacityStorage.isBoardingPossible(distanceOfRailCarriage.getLeft())) {
                    capacityStorage.inPassenger(distanceOfRailCarriage.getLeft(), passenger);
                    addToSolutionCost(distanceOfRailCarriage.getRight());
                    break;
                }
            }
        }
        return solutionCost;
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
