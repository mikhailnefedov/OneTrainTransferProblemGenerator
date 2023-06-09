package com.github.onetraintransferproblemgenerator.orchestration;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.serialization.InstanceToCSVWriter;

import java.util.ArrayList;
import java.util.List;

public class SimpleOrchestration {

    private final OrchestrationParameters parameters;

    public SimpleOrchestration(OrchestrationParameters parameters) {
        this.parameters = parameters;
    }

    public void runOrchestration() {
        List<OneTrainTransferProblem> instances = generateInstances();
        List<InstanceFeatureDescription> featureDescriptions = generateFeatureDescriptions(instances);
        serializeToCsv(featureDescriptions);
    }

    private List<OneTrainTransferProblem> generateInstances() {
        List<OneTrainTransferProblem> instances = new ArrayList<>();
        for (int i = 0; i < parameters.getInstanceCount(); i++) {
            instances.add(parameters.getGenerator().generate());
        }
        return instances;
    }

    private List<InstanceFeatureDescription> generateFeatureDescriptions(List<OneTrainTransferProblem> instances) {
        List<InstanceFeatureDescription> descriptions = new ArrayList<>();
        for (int i = 0; i < instances.size(); i++) {
            String instanceId = String.format("%s%d",parameters.getInstanceIdPrefix(), i);
            InstanceFeatureDescription description = FeatureExtractor.extract(instanceId, instances.get(i));
            description.setSource(parameters.getGenerator().getClass().getSimpleName());
            descriptions.add(description);
        }
        return descriptions;
    }

    private void serializeToCsv(List<InstanceFeatureDescription> descriptions) {
        InstanceToCSVWriter.writeToCSV(descriptions, parameters.getCsvFilePath());
    }

}
