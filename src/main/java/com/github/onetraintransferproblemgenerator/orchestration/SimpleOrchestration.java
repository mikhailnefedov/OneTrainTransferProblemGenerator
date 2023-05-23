package com.github.onetraintransferproblemgenerator.orchestration;

import com.github.onetraintransferproblemgenerator.exceptions.NotEnoughTrainCapacityException;
import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.persistence.IdGenerator;
import com.github.onetraintransferproblemgenerator.persistence.MongoClientConfiguration;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.serialization.InstanceToCSVWriter;
import com.github.onetraintransferproblemgenerator.solvers.FirstAvailableCarriageSolver;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.validation.InstanceValidator;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class SimpleOrchestration {

    private final int INSTANCE_COUNT = 150;
    private OneTrainTransferProblemGenerator generator;
    private InstanceValidator validator;
    private List<OneTrainTransferSolver> solvers;
    private MongoClient mongoClient;
    private ProblemInstanceRepository problemInstanceRepository;

    public SimpleOrchestration(OneTrainTransferProblemGenerator generator) {
        this.generator = generator;
        validator = new InstanceValidator();
        mongoClient = MongoClientConfiguration.configureMongoClient();
        problemInstanceRepository = new ProblemInstanceRepository(mongoClient.getDatabase("OneTrainTransfer"));
    }

    public void runOrchestration() {
        List<OneTrainTransferProblem> instances = generateInstances();
        validateInstances(instances);
        List<InstanceFeatureDescription> featureDescriptions = generateFeatureDescriptions(instances);
        featureDescriptions = solveInstances(instances, featureDescriptions);
        saveToMongoDB(instances, featureDescriptions);
        serializeToCsv(featureDescriptions);

        mongoClient.close();
    }

    private List<OneTrainTransferProblem> generateInstances() {
        List<OneTrainTransferProblem> instances = new ArrayList<>();
        for (int i = 0; i < INSTANCE_COUNT; i++) {
            instances.add(generator.generate());
        }
        return instances;
    }

    @SneakyThrows
    private void validateInstances(List<OneTrainTransferProblem> instances) {
        for (OneTrainTransferProblem instance : instances) {
            boolean isValidated = validator.validateInstance(instance);
            if (!isValidated) {
                throw new NotEnoughTrainCapacityException(instance);
            }
        }
    }

    private List<InstanceFeatureDescription> generateFeatureDescriptions(List<OneTrainTransferProblem> instances) {
        List<InstanceFeatureDescription> descriptions = new ArrayList<>();
        for (int i = 0; i < instances.size(); i++) {
            String instanceId = String.format("auto_%d", i);
            InstanceFeatureDescription description = FeatureExtractor.extract(instanceId, instances.get(i));

            descriptions.add(description);
        }
        return descriptions;
    }

    //TODO: Refactor to better use of multiple solvers
    private List<InstanceFeatureDescription> solveInstances(List<OneTrainTransferProblem> instances, List<InstanceFeatureDescription> featureDescriptions) {
        for (int i = 0; i < instances.size(); i++) {
            OneTrainTransferSolver solver = new FirstAvailableCarriageSolver(instances.get(i));
            double greedyCost = solver.solve();
            featureDescriptions.get(i).setFirstAvailableCarriageCost(greedyCost);
        }
        return featureDescriptions;
    }

    private void saveToMongoDB(List<OneTrainTransferProblem> instances, List<InstanceFeatureDescription> featureDescriptions) {
        String experimentId = IdGenerator.generateExperimentId();
        List<ProblemInstance> problemInstances = new ArrayList<>();
        for (int i = 0; i < instances.size(); i++) {
            ProblemInstance problemInstance = new ProblemInstance(instances.get(i), featureDescriptions.get(i), experimentId);
            problemInstances.add(problemInstance);
        }
        problemInstanceRepository.getCollection().insertMany(problemInstances);
    }

    private void serializeToCsv(List<InstanceFeatureDescription> descriptions) {
        InstanceToCSVWriter.writeCSV(descriptions);
    }

}
