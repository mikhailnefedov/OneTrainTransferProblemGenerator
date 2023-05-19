package com.github.onetraintransferproblemgenerator.orchestration;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.generation.SimpleGenerator;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Platform;
import com.github.onetraintransferproblemgenerator.serialization.InstanceToCSVWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleOrchestration {

    private static final int INSTANCE_COUNT = 150;

    public static void main(String[] args) {

        List<InstanceFeatureDescription> descriptions = generateInstances();
        serialize(descriptions);
    }

    private static List<InstanceFeatureDescription> generateInstances() {
        List<InstanceFeatureDescription> descriptions = new ArrayList<>();
        for (int i = 0; i < INSTANCE_COUNT; i++) {
            descriptions.add(generateInstance(i));
        }
        return descriptions;
    }

    private static InstanceFeatureDescription generateInstance(int generationId) {
        String instanceId = String.format("auto_%d", generationId);
        OneTrainTransferProblem problem = SimpleGenerator.generateSimpleScenario();
        InstanceFeatureDescription description = FeatureExtractor.extract("auto_1", problem);

        description.setInstanceId(instanceId);

        Random random = new Random();
        description.setGreedyResult(random.nextDouble(9, 20));
        description.setTestResult(random.nextDouble(0, 13));
        return description;
    }

    private static void serialize(List<InstanceFeatureDescription> descriptions) {
        InstanceToCSVWriter.writeCSV(descriptions);
    }
}
