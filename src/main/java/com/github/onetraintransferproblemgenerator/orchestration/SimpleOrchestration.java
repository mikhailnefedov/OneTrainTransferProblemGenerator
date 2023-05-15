package com.github.onetraintransferproblemgenerator.orchestration;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.generation.SimpleGenerator;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Platform;
import com.github.onetraintransferproblemgenerator.serialization.InstanceToCSVWriter;

import java.util.ArrayList;
import java.util.List;

public class SimpleOrchestration {

    public static void main(String[] args) {

        List<InstanceFeatureDescription> descriptions = generateInstances();
        serialize(descriptions);
    }

    private static List<InstanceFeatureDescription> generateInstances() {
        List<InstanceFeatureDescription> descriptions = new ArrayList<>();
        OneTrainTransferProblem problem = SimpleGenerator.generateSimpleScenario();
        descriptions.add(FeatureExtractor.extract("auto_1", problem));
        problem = SimpleGenerator.generateSimpleScenario();
        descriptions.add(FeatureExtractor.extract("auto_2", problem));
        return descriptions;
    }

    private static void serialize(List<InstanceFeatureDescription> descriptions) {
        InstanceToCSVWriter.writeCSV(descriptions);
    }
}
