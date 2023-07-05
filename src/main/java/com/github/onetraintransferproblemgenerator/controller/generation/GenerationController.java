package com.github.onetraintransferproblemgenerator.controller.generation;

import com.github.onetraintransferproblemgenerator.controller.generation.GenerationParameters;
import com.github.onetraintransferproblemgenerator.controller.generation.InstanceGeneration;
import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.serialization.CsvExporter;
import com.github.onetraintransferproblemgenerator.serialization.InstanceToCSVWriter;
import com.github.onetraintransferproblemgenerator.solvers.CostComputer;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.EvolutionarySolver;
import com.github.onetraintransferproblemgenerator.validation.InstanceValidator;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("generation")
public class GenerationController {

    private GenerationParameters generationParameters;
    private final ProblemInstanceRepository problemInstanceRepository;

    public GenerationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
    }

    @PostMapping("generateinstances")
    void generateInstances(@RequestBody GenerationParameters generationParameters) {
        this.generationParameters = generationParameters;
        List<ProblemInstance> instances = generateInstances(generationParameters.getGenerators());
        generateFeatureDescriptions(instances);
        solveInstances(instances);
        if (generationParameters.isStoreInstances()) {
            problemInstanceRepository.saveAll(instances);
        }
        CsvExporter.exportToCsv(instances, generationParameters.getExperimentId());
        System.out.println("Finished generation!");
    }

    private List<ProblemInstance> generateInstances(Map<String, InstanceGeneration> generators) {
        List<ProblemInstance> problemInstances = new ArrayList<>();
        for (String generatorName : generators.keySet()) {
            try {
                OneTrainTransferProblemGenerator generator = createGenerator(generatorName);
                InstanceGeneration generatorParameter = generators.get(generatorName);
                System.out.println("Start generation for " + generatorName);
                for (int i = 1; i <= generatorParameter.getInstanceCount(); i++) {
                    ProblemInstance instance = generateInstance(generator, String.format("%s%d", generatorParameter.getIdPrefix(), i));
                    problemInstances.add(instance);
                    if (i % 50 == 0) {
                        int remainingInstances = generatorParameter.getInstanceCount() - i;
                        System.out.println("remaining instances to generate: " + remainingInstances);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return problemInstances;
    }

    private OneTrainTransferProblemGenerator createGenerator(String generatorName) throws ClassNotFoundException {
        try {
            Class<? extends OneTrainTransferProblemGenerator> generatorClass =
                    Class.forName(generatorName).asSubclass(OneTrainTransferProblemGenerator.class);
            Constructor<? extends OneTrainTransferProblemGenerator> con = generatorClass.getConstructor();
            return con.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new ClassNotFoundException();
    }

    private ProblemInstance generateInstance(OneTrainTransferProblemGenerator generator, String instanceId) {
        OneTrainTransferProblem instance = generator.generate();
        validateInstance(instance);
        return new ProblemInstance(instance, generationParameters.getExperimentId(), generator.getClass(), instanceId);
    }

    @SneakyThrows
    private void validateInstance(OneTrainTransferProblem instance) {
        InstanceValidator.validateInstance(instance);
    }

    private void generateFeatureDescriptions(List<ProblemInstance> instances) {
        for (ProblemInstance instance : instances) {
            InstanceFeatureDescription description = FeatureExtractor.extract(instance.getInstanceId(), instance.getProblem());
            String source = instance.getFeatureDescription().getSource();
            instance.setFeatureDescription(description);
            instance.getFeatureDescription().setSource(source);
        }
    }

    private void solveInstances(List<ProblemInstance> instances) {
        instances.parallelStream().forEach(instance -> {
            HashMap<Class<? extends OneTrainTransferSolver>, HashMap<Passenger, Integer>> solverSolutionMapping = new HashMap<>();
            for (String solverName : generationParameters.getSolvers()) {
                try {
                    Class<? extends OneTrainTransferSolver> solverClass = Class.forName(solverName).asSubclass(OneTrainTransferSolver.class);
                    if (solverClass.equals(EvolutionarySolver.class)) {
                        solverSolutionMapping.put(solverClass, handleEvolutionarySolver(instance, solverSolutionMapping, solverClass));
                    } else {
                        solverSolutionMapping.put(solverClass, handleOtherSolvers(solverClass, instance));
                    }
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            setInstanceAlgorithmCosts(instance, solverSolutionMapping);
        });
    }

    private HashMap<Passenger, Integer> handleEvolutionarySolver(ProblemInstance instance, HashMap<Class<? extends OneTrainTransferSolver>, HashMap<Passenger, Integer>> solverSolutionMapping, Class<? extends OneTrainTransferSolver> solverClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        System.out.println("Start EvolutionSolver for Instance " + instance.getInstanceId());

        int passengerCount = instance.getProblem().getPassengers().size();
        List<HashMap<Passenger, Integer>> knownSolutions = solverSolutionMapping.values().stream()
                .filter(solutionMapping -> solutionMapping.keySet().size() == passengerCount)
                .collect(Collectors.toList());
        Constructor<? extends OneTrainTransferSolver> con = solverClass.getConstructor(OneTrainTransferProblem.class, knownSolutions.getClass());
        OneTrainTransferSolver solver = con.newInstance(instance.getProblem(), knownSolutions);
        return solver.solve();
    }

    private HashMap<Passenger, Integer> handleOtherSolvers(Class<? extends OneTrainTransferSolver> solverClass, ProblemInstance instance) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<? extends OneTrainTransferSolver> con = solverClass.getConstructor(OneTrainTransferProblem.class);
        OneTrainTransferSolver solver = con.newInstance(instance.getProblem());
        return solver.solve();
    }

    private void setInstanceAlgorithmCosts(ProblemInstance instance,
                                           HashMap<Class<? extends OneTrainTransferSolver>, HashMap<Passenger, Integer>> solverSolutionMapping) {
        CostComputer costComputer = new CostComputer(instance.getProblem());
        for (Class<? extends OneTrainTransferSolver> solverClass : solverSolutionMapping.keySet()) {
            double cost = costComputer.computeCost(solverSolutionMapping.get(solverClass));
            instance.getFeatureDescription().setAlgorithmCost(cost, solverClass);
        }
    }

}
