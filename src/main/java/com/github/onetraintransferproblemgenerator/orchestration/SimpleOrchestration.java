package com.github.onetraintransferproblemgenerator.orchestration;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.persistence.IdGenerator;
import com.github.onetraintransferproblemgenerator.persistence.MongoClientConfiguration;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.serialization.InstanceToCSVWriter;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.validation.InstanceValidator;
import com.mongodb.client.MongoClient;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SimpleOrchestration {

    private final OrchestrationParameters parameters;
    private MongoClient mongoClient;
    private ProblemInstanceRepository problemInstanceRepository;

    public SimpleOrchestration(OrchestrationParameters parameters) {
        this.parameters = parameters;
        mongoClient = MongoClientConfiguration.configureMongoClient();
        problemInstanceRepository = new ProblemInstanceRepository(mongoClient.getDatabase("OneTrainTransfer"));
    }

    public void runOrchestration() {
        List<OneTrainTransferProblem> instances = generateInstances();
        List<InstanceFeatureDescription> featureDescriptions = generateFeatureDescriptions(instances);
        //saveToMongoDB(instances, featureDescriptions);
        serializeToCsv(featureDescriptions);

        mongoClient.close();
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

    private void saveToMongoDB(List<OneTrainTransferProblem> instances, List<InstanceFeatureDescription> featureDescriptions) {
        String experimentId = IdGenerator.generateExperimentId();
        List<ProblemInstance> problemInstances = new ArrayList<>();
        for (int i = 0; i < instances.size(); i++) {
            //ProblemInstance problemInstance = new ProblemInstance(instances.get(i), featureDescriptions.get(i), experimentId);
            //problemInstances.add(problemInstance);
        }
        problemInstanceRepository.getCollection().insertMany(problemInstances);
    }

    private void serializeToCsv(List<InstanceFeatureDescription> descriptions) {
        InstanceToCSVWriter.writeToCSV(descriptions, parameters.getCsvFilePath());
    }

}
