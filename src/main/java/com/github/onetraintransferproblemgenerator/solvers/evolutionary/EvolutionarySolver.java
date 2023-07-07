package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.solvers.CostComputer;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.HistoricalEvolutionData;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.SolutionAndHistoryData;

public abstract class EvolutionarySolver extends OneTrainTransferSolver {

    //Configuration of evolutionary algorithms
    protected final int POPULATION_SIZE = 50;
    protected final int PARENTS_COUNT = 20;
    protected final int CHILDREN_COUNT = 50;
    protected final int GENERATION_COUNT = 200;
    protected final double MUTATION_RATE = 0.3;

    protected CostComputer costComputer;
    protected double bestKnownFitnessScore = Double.MAX_VALUE;
    protected HistoricalEvolutionData historicalData;

    public EvolutionarySolver(OneTrainTransferProblem problem) {
        super(problem);
        historicalData = new HistoricalEvolutionData();
    }

    public abstract SolutionAndHistoryData solveAndGenerateHistoricalData();
}
