package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.SolutionAndHistoryData;

public abstract class EvolutionarySolver extends OneTrainTransferSolver {

    public EvolutionarySolver(OneTrainTransferProblem problem) {
        super(problem);
    }

    public abstract SolutionAndHistoryData solveAndGenerateHistoricalData();
}
