package com.github.onetraintransferproblemgenerator.solvers.greedyall;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriageDistance;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

//TODO: FAULTY --> Theoretically possible to seat passengers, but passenger would net to change rail carriage (not possible)
public class GreedyAllPassengersSolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;
    private SectionCapacityRailCarriageStorage sectionCapacityRailCarriageStorage;

    public GreedyAllPassengersSolver(OneTrainTransferProblem problem) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        sectionCapacityRailCarriageStorage = new SectionCapacityRailCarriageStorage(problem.getTrain());
    }

    @Override
    public HashMap<Passenger, Integer> solve() {
        List<Tuple<Passenger, List<RailCarriageDistance>>> passengersAndTheirRailCarriageDistances = problem.getPassengers().stream()
                .map(p -> {
                    List<RailCarriageDistance> railCarriageDistances = carriagePositionHelper.getDistancesForRailCarriages(p);
                    railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCost));
                    return new Tuple<>(p, railCarriageDistances);
                })
                .sorted(Comparator.comparingDouble(t -> t.getRight().get(0).getCost()))
                .collect(Collectors.toList());

        for (Tuple<Passenger, List<RailCarriageDistance>> tuple : passengersAndTheirRailCarriageDistances) {
            for (RailCarriageDistance railCarriageDistance : tuple.getRight()) {
                if (sectionCapacityRailCarriageStorage.isBoardingPossible(railCarriageDistance.getRailCarriageId(), tuple.getLeft())) {
                    sectionCapacityRailCarriageStorage.inPassenger(railCarriageDistance.getRailCarriageId(), tuple.getLeft());
                    addToSolution(tuple.getLeft(), railCarriageDistance.getRailCarriageId());
                    break;
                }
            }
        }
        return solutionMapping;
    }

}
