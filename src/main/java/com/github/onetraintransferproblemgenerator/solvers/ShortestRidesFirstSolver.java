package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.greedyall.SectionCapacityRailCarriageStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShortestRidesFirstSolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;
    private SectionCapacityRailCarriageStorage sectionCapacityRailCarriageStorage;

    public ShortestRidesFirstSolver(OneTrainTransferProblem problem) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        sectionCapacityRailCarriageStorage = new SectionCapacityRailCarriageStorage(problem.getTrain());
    }

    @Override
    public double solve() {
        List<Passenger> passengers = problem.getPassengers().stream()
                .map(p -> {
                    int rideLength = p.getOutStation() - p.getInStation();
                    return new Tuple<>(p, rideLength);
                })
                .sorted(Comparator.comparingInt(Tuple::getRight))
                .map(Tuple::getLeft)
                .collect(Collectors.toList());

        for (Passenger passenger : passengers) {
            List<RailCarriageDistance> railCarriageDistances = carriagePositionHelper.getDistanceIfRailCarriageIsUsed(passenger);
            railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCombinedDistances));

            for (RailCarriageDistance railCarriageDistance : railCarriageDistances) {
                if (sectionCapacityRailCarriageStorage.isBoardingPossible(railCarriageDistance.getRailCarriageId(), passenger)) {
                    sectionCapacityRailCarriageStorage.inPassenger(railCarriageDistance.getRailCarriageId(), passenger);
                    addToSolutionCost(railCarriageDistance.getInDistance());
                    addToSolutionCost(railCarriageDistance.getOutDistance());
                    break;
                }
            }
        }

        return solutionCost;
    }
}
