package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.CostComputer;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;
import com.github.onetraintransferproblemgenerator.solvers.RandomPassengerOrderSolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.SolutionAndHistoryData;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.Crossover;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation.FreeCapacitySwapMutation;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.TournamentSelection;

import java.util.*;

/**
 * Starts from randomly generated in order solutions
 */
public class RandomSolutionsEvolutionarySolver extends EvolutionarySolver {

    private RailCarriagePositionHelper carriagePositionHelper;
    private List<HashMap<Passenger, Integer>> knownSolutions;
    private Crossover crossover;
    private FreeCapacitySwapMutation freeCapacitySwapMutation;
    private RandomPassengerOrderSolver randomPassengerOrderSolver;


    public RandomSolutionsEvolutionarySolver(OneTrainTransferProblem problem, ArrayList<HashMap<Passenger, Integer>> knownSolutions) {
        super(problem, RandomSolutionsEvolutionarySolver.class.getSimpleName());

        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        costComputer = new CostComputer(problem);
        this.knownSolutions = knownSolutions;
        crossover = new Crossover(problem, carriagePositionHelper);
        freeCapacitySwapMutation = new FreeCapacitySwapMutation();
        randomPassengerOrderSolver = new RandomPassengerOrderSolver(problem);
    }

    @Override
    public HashMap<Passenger, Integer> solve() {
        return solveAndGenerateHistoricalData().getPassengerMapping();
    }

    @Override
    public SolutionAndHistoryData solveAndGenerateHistoricalData() {
        List<Individual> generation = initializeStartPopulation();
        for (int i = 1; i <= GENERATION_COUNT; i++) {
            List<Individual> parents = TournamentSelection.select(generation, PARENTS_COUNT);
            Random random = new Random();
            List<Individual> children = new ArrayList<>();
            for (int j = 0; j < CHILDREN_COUNT; j++) {
                Individual parent1 = parents.get(random.nextInt(parents.size()));
                Individual parent2 = parents.get(random.nextInt(parents.size()));
                Individual child = crossover.doCrossover(parent1, parent2);

                double randomDouble = random.nextDouble();
                if (randomDouble < MUTATION_RATE)
                    freeCapacitySwapMutation.mutate(child);

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
        while (individuals.size() < POPULATION_SIZE) {
            HashMap<Passenger, Integer> solution = randomPassengerOrderSolver.solve();
            individuals.add(createIndividual(solution));
        }
        updateBestKnownIndividual(individuals);

        return individuals;
    }

}
