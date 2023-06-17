package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.CostComputer;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;

import java.util.*;

public class EvolutionarySolver extends OneTrainTransferSolver {

    private RailCarriagePositionHelper carriagePositionHelper;
    private CostComputer costComputer;
    private final int populationSize = 50;
    private final int parentCount = 20;
    private final int childrenCount = 50;
    private final int generationCount = 200;
    private final double mutationRate = 0.3;
    private List<HashMap<Passenger, Integer>> knownSolutions;
    private Crossover crossover;
    private Mutation mutation;
    private Individual bestKnownIndividual;
    private double bestKnownFitnessScore = Double.MAX_VALUE;

    //TODO: currently copy paste from greedy all, better structure for solver evaluation necessary
    // --> give back solution not the cost and evaluate the cost afterwards
    public EvolutionarySolver(OneTrainTransferProblem problem, List<HashMap<Passenger, Integer>> knownSolutions) {
        super(problem);
        carriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        costComputer = new CostComputer(problem);
        this.knownSolutions = knownSolutions;
        crossover = new Crossover(problem, carriagePositionHelper);
        mutation = new Mutation();
    }

    private List<Individual> initializeStartPopulation() {
        List<Individual> individuals = new ArrayList<>();
        while (individuals.size() < populationSize) {
            for (HashMap<Passenger, Integer> solution : knownSolutions) {
                individuals.add(createIndividual(solution));
                if (individuals.size() == populationSize)
                    break;
            }
        }
        updateBestKnownIndividual(individuals);

        return individuals;
    }

    private Individual createIndividual(HashMap<Passenger, Integer> solution) {
        Individual individual = new Individual(solution, problem);
        double fitness = costComputer.computeCost(solution);
        individual.setFitness(fitness);
        return individual;
    }

    private void updateBestKnownIndividual(List<Individual> individuals) {
        Individual bestIndividual = individuals.stream().min(Comparator.comparing(Individual::getFitness)).get();
        if (bestIndividual.getFitness() < bestKnownFitnessScore) {
            bestKnownFitnessScore = bestIndividual.getFitness();
            bestKnownIndividual = bestIndividual;
        }
    }

    @Override
    public HashMap<Passenger, Integer> solve() {
        List<Individual> generation = initializeStartPopulation();
        for (int i = 1; i <= generationCount; i++) {
            List<Individual> parents = TournamentSelection.select(generation, parentCount);
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
        return bestKnownIndividual.getPassengerRailCarriageMapping();
    }

}
