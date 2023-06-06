package com.github.onetraintransferproblemgenerator.orchestration;

import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import lombok.Data;

import java.util.List;

@Data
public class OrchestrationParameters {
    private String csvFilePath = "./metadata.csv";
    private int instanceCount = 150;
    private String instanceIdPrefix = "auto_";
    private OneTrainTransferProblemGenerator generator;
    private List<Class<? extends OneTrainTransferSolver>> solvers;
}
