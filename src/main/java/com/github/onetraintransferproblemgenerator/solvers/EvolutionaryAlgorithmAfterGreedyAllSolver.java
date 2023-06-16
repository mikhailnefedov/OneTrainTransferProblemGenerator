package com.github.onetraintransferproblemgenerator.solvers;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.greedyall.SectionCapacityRailCarriageStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EvolutionaryAlgorithmAfterGreedyAllSolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;
    private SectionCapacityRailCarriageStorage sectionCapacityRailCarriageStorage;

    //TODO: currently copy paste from greedy all, better structure for solver evaluation necessary
    // --> give back solution not the cost and evaluate the cost afterwards
    public EvolutionaryAlgorithmAfterGreedyAllSolver(OneTrainTransferProblem problem) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        sectionCapacityRailCarriageStorage = new SectionCapacityRailCarriageStorage(problem.getTrain());
    }

    @Override
    public double solve() {
        HashMap<Passenger, Integer> passengerCarriageIdMapping = greedyAllAlgorithm();
        //TODO: evolutionary algorithm
        return 0.0;
    }

    private HashMap<Passenger, Integer> greedyAllAlgorithm() {
        List<Tuple<Passenger, List<RailCarriageDistance>>> passengersAndTheirRailCarriageDistances = problem.getPassengers().stream()
                .map(p -> {
                    List<RailCarriageDistance> railCarriageDistances = carriagePositionHelper.getDistancesForRailCarriages(p);
                    railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCombinedDistances));
                    return new Tuple<>(p, railCarriageDistances);
                })
                .sorted(Comparator.comparingInt(t -> t.getRight().get(0).getCombinedDistances()))
                .collect(Collectors.toList());

        HashMap<Passenger, Integer> passengerRailCarriageIdTuples = new HashMap<>();
        for (Tuple<Passenger, List<RailCarriageDistance>> tuple : passengersAndTheirRailCarriageDistances) {
            for(RailCarriageDistance railCarriageDistance :tuple.getRight()) {
                if (sectionCapacityRailCarriageStorage.isBoardingPossible(railCarriageDistance.getRailCarriageId(), tuple.getLeft())) {
                    passengerRailCarriageIdTuples.put(tuple.getLeft(), railCarriageDistance.getRailCarriageId());
                    break;
                }
            }
        }

        return passengerRailCarriageIdTuples;
    }
}
