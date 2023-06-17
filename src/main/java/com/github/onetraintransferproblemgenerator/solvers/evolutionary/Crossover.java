package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriageDistance;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;

import java.util.List;
import java.util.Random;

public class Crossover {

    private OneTrainTransferProblem problem;
    private int stationCount;
    private RailCarriagePositionHelper railCarriagePositionHelper;
    private Random random;

    public Crossover(OneTrainTransferProblem problem, RailCarriagePositionHelper railCarriagePositionHelper) {
        this.problem = problem;
        stationCount = problem.getTrain().getStationCount();
        this.railCarriagePositionHelper = railCarriagePositionHelper;
        random = new Random();
    }

    public Individual doCrossover(Individual parent1, Individual parent2) {
        int crossoverPoint = random.nextInt(1, stationCount);

        List<Passenger> passengersFromFirstParent =
                problem.getPassengers().stream().filter(p -> p.getInStation() < crossoverPoint).toList();
        List<Passenger> passengersFromSecondParent =
                problem.getPassengers().stream().filter(p -> p.getInStation() >= crossoverPoint).toList();

        Individual child = new Individual(problem);
        for (Passenger passenger : passengersFromFirstParent) {
            child.addPassengerWithRailCarriageId(passenger, parent1.getRailCarriageIdOfPassenger(passenger));
        }
        for (Passenger passenger : passengersFromSecondParent) {
            List<Integer> greedyRailCarriages = railCarriagePositionHelper.getDistancesForRailCarriages(passenger).stream()
                    .map(RailCarriageDistance::getRailCarriageId)
                    .toList();
            child.addPassengerWithoutRailCarriage(passenger, greedyRailCarriages);
        }
        return child;
    }
}
