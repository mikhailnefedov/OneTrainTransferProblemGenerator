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
    protected int populationSize;
    protected int parentsCount;
    protected int childrenCount;
    protected int generationCount;
    protected double mutationRate;

    protected CostComputer costComputer;
    protected Individual bestKnownIndividual;
    protected double bestKnownFitnessScore = Double.MAX_VALUE;
    protected HistoricalEvolutionData historicalData;

    public EvolutionarySolver(OneTrainTransferProblem problem, String solverName, SolverConfiguration solverConfiguration) {
        super(problem);

        populationSize = solverConfiguration.getPopulationSize();
        parentsCount = solverConfiguration.getParentsCount();
        childrenCount = solverConfiguration.getChildrenCount();
        generationCount = solverConfiguration.getGenerationCount();
        mutationRate = solverConfiguration.getMutationRate();

        historicalData = new HistoricalEvolutionData(solverName);
    }

    public abstract SolutionAndHistoryData solveAndGenerateHistoricalData();

    protected Individual createIndividual(HashMap<Passenger, Integer> solution) {
        Individual individual = new Individual(solution, problem);
        double fitness = costComputer.computeCost(solution);
        individual.setFitness(fitness);
        return individual;
    }

    /**
     *
     * @param individuals
     * @return true if there is a new best individual
     */
    protected boolean updateBestKnownIndividual(List<Individual> individuals) {
        Individual bestIndividual = individuals.stream().min(Comparator.comparing(Individual::getFitness)).get();
        boolean newBestIndividual = false;
        if (bestIndividual.getFitness() < bestKnownFitnessScore) {
            bestKnownFitnessScore = bestIndividual.getFitness();
            bestKnownIndividual = bestIndividual;
            newBestIndividual = true;
        }
        historicalData.addBestCost(bestKnownFitnessScore);
        return newBestIndividual;
    }
}
