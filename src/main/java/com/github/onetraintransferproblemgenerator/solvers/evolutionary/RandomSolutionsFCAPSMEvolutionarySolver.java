package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.CostComputer;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;
import com.github.onetraintransferproblemgenerator.solvers.RandomPassengerOrderSolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.SolutionAndHistoryData;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.Crossover;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.TournamentSelection;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation.FreeCapacityAndPassengerSwapMutation;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation.FreeCapacitySwapMutation;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation.Mutation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomSolutionsFCAPSMEvolutionarySolver extends EvolutionarySolver {

    private RailCarriagePositionHelper carriagePositionHelper;
    private final Crossover crossover;
    private final Mutation mutation;
    private RandomPassengerOrderSolver randomPassengerOrderSolver;


    public RandomSolutionsFCAPSMEvolutionarySolver(OneTrainTransferProblem problem,
                                                ArrayList<HashMap<Passenger, Integer>> knownSolutions,
                                                SolverConfiguration solverConfiguration) {
        super(problem, RandomSolutionsFCAPSMEvolutionarySolver.class.getSimpleName(), solverConfiguration);

        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        costComputer = new CostComputer(problem);
        crossover = new Crossover(problem, carriagePositionHelper);
        randomPassengerOrderSolver = new RandomPassengerOrderSolver(problem);
        mutation = new FreeCapacityAndPassengerSwapMutation();
    }

    @Override
    public HashMap<Passenger, Integer> solve() {
        return solveAndGenerateHistoricalData().getPassengerMapping();
    }

    @Override
    public SolutionAndHistoryData solveAndGenerateHistoricalData() {
        List<Individual> generation = initializeStartPopulation();
        for (int i = 1; i <= generationCount; i++) {
            List<Individual> parents = TournamentSelection.select(generation, parentsCount);
            Random random = new Random();
            List<Individual> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                Individual parent1 = parents.get(random.nextInt(parents.size()));
                Individual parent2 = parents.get(random.nextInt(parents.size()));
                Individual child = crossover.doCrossover(parent1, parent2);

                double randomDouble = random.nextDouble();
                if (randomDouble < mutationRate)
                    mutation.mutate(child);

                double fitness = costComputer.computeCost(child.getPassengerRailCarriageMapping());
                child.setFitness(fitness);
                children.add(child);
            }
            generation = children;
            updateBestKnownIndividual(generation);
        }
        return new SolutionAndHistoryData(bestKnownIndividual.getPassengerRailCarriageMapping(), historicalData);
    }

    private List<Individual> initializeStartPopulation() {
        List<Individual> individuals = new ArrayList<>();
        while (individuals.size() < populationSize) {
            HashMap<Passenger, Integer> solution = randomPassengerOrderSolver.solve();
            individuals.add(createIndividual(solution));
        }
        updateBestKnownIndividual(individuals);

        return individuals;
    }
}
