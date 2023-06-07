package com.github.onetraintransferproblemgenerator.solvers.greedyall;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriageDistance;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;

import java.util.Comparator;
import java.util.List;

public class GreedyAllPassengersSolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;
    private SectionCapacityRailCarriageStorage sectionCapacityRailCarriageStorage;

    public GreedyAllPassengersSolver(OneTrainTransferProblem problem) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        sectionCapacityRailCarriageStorage = new SectionCapacityRailCarriageStorage(problem.getTrain());
    }

    @Override
    public double solve() {
        List<Tuple<Passenger, List<RailCarriageDistance>>> passengersAndTheirRailCarriageDistances = problem.getPassengers().stream()
                .map(p -> {
                    List<RailCarriageDistance> railCarriageDistances = carriagePositionHelper.getDistanceIfRailCarriageIsUsed(p);
                    railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCombinedDistances));
                    return new Tuple<>(p, railCarriageDistances);
                }).toList();

        passengersAndTheirRailCarriageDistances.sort(Comparator.comparingInt(t -> t.getRight().get(0).getCombinedDistances()));

        for (Tuple<Passenger, List<RailCarriageDistance>> tuple : passengersAndTheirRailCarriageDistances) {
            for(RailCarriageDistance railCarriageDistance :tuple.getRight()) {
                if (sectionCapacityRailCarriageStorage.isBoardingPossible(railCarriageDistance.getRailCarriageId(), tuple.getLeft())) {
                    sectionCapacityRailCarriageStorage.inPassenger(railCarriageDistance.getRailCarriageId(), tuple.getLeft());
                    addToSolutionCost(railCarriageDistance.getInDistance());
                    addToSolutionCost(railCarriageDistance.getOutDistance());
                    break;
                }
            }
        }

        return solutionCost;
    }

}
