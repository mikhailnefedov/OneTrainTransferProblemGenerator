package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.CostComputer;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.HistoricalEvolutionData;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.SolutionAndHistoryData;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public abstract class EvolutionarySolver extends OneTrainTransferSolver {

    //Configuration of evolutionary algorithms
    protected final int POPULATION_SIZE = 50;
    protected final int PARENTS_COUNT = 20;
    protected final int CHILDREN_COUNT = 50;
    protected final int GENERATION_COUNT = 200;
    protected final double MUTATION_RATE = 0.3;

    protected CostComputer costComputer;
    protected Individual bestKnownIndividual;
    protected double bestKnownFitnessScore = Double.MAX_VALUE;
    protected HistoricalEvolutionData historicalData;

    public EvolutionarySolver(OneTrainTransferProblem problem, String solverName) {
        super(problem);
        historicalData = new HistoricalEvolutionData(solverName);
    }

    public abstract SolutionAndHistoryData solveAndGenerateHistoricalData();

    protected Individual createIndividual(HashMap<Passenger, Integer> solution) {
        Individual individual = new Individual(solution, problem);
        double fitness = costComputer.computeCost(solution);
        individual.setFitness(fitness);
        return individual;
    }

    protected void updateBestKnownIndividual(List<Individual> individuals) {
        Individual bestIndividual = individuals.stream().min(Comparator.comparing(Individual::getFitness)).get();
        if (bestIndividual.getFitness() < bestKnownFitnessScore) {
            bestKnownFitnessScore = bestIndividual.getFitness();
            bestKnownIndividual = bestIndividual;
        }
        historicalData.addBestCost(bestKnownFitnessScore);
    }
}
