package com.github.onetraintransferproblemgenerator.orchestration;

import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import lombok.Data;

@Data
public class OrchestrationParameters {
    private String csvFilePath = "./metadata.csv";
    private int instanceCount = 150;
    private String instanceIdPrefix = "auto_";
    private OneTrainTransferProblemGenerator generator;
}
