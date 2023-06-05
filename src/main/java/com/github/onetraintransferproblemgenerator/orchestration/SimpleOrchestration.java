package com.github.onetraintransferproblemgenerator.orchestration;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.persistence.IdGenerator;
import com.github.onetraintransferproblemgenerator.persistence.MongoClientConfiguration;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.serialization.InstanceToCSVWriter;
import com.github.onetraintransferproblemgenerator.solvers.FirstAvailableCarriageSolver;
import com.github.onetraintransferproblemgenerator.solvers.GreedySolver;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.validation.InstanceValidator;
import com.mongodb.client.MongoClient;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class SimpleOrchestration {

    private final OrchestrationParameters parameters;
    private InstanceValidator validator;
    private List<OneTrainTransferSolver> solvers;
    private MongoClient mongoClient;
    private ProblemInstanceRepository problemInstanceRepository;

    public SimpleOrchestration(OrchestrationParameters parameters) {
        this.parameters = parameters;
        validator = new InstanceValidator();
        mongoClient = MongoClientConfiguration.configureMongoClient();
        problemInstanceRepository = new ProblemInstanceRepository(mongoClient.getDatabase("OneTrainTransfer"));
    }

    public void runOrchestration() {
        List<OneTrainTransferProblem> instances = generateInstances();
        validateInstances(instances);
        List<InstanceFeatureDescription> featureDescriptions = generateFeatureDescriptions(instances);
        featureDescriptions = solveInstances(instances, featureDescriptions);
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

    @SneakyThrows
    private void validateInstances(List<OneTrainTransferProblem> instances) {
        for (OneTrainTransferProblem instance : instances) {
            validator.validateInstance(instance);
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
            double firstRailCarriageCost = solver.solve();
            featureDescriptions.get(i).setFirstAvailableCarriageCost(firstRailCarriageCost);

            solver = new GreedySolver(instances.get(i));
            double greedyCost = solver.solve();
            featureDescriptions.get(i).setGreedyCost(greedyCost);
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
        InstanceToCSVWriter.writeCSV(descriptions, parameters.getCsvFilePath());
    }

}
