package com.github.onetraintransferproblemgenerator.controller.solver;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.persistence.HistoricalEvolutionDataRepository;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.solvers.CostComputer;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.EvolutionarySolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.SolverConfiguration;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.HistoricalEvolutionData;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.SolutionAndHistoryData;
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
@RequestMapping("evolutionary")
public class EvolutionarySolverController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final HistoricalEvolutionDataRepository historicalEvolutionDataRepository;

    public EvolutionarySolverController(ProblemInstanceRepository problemInstanceRepository,
                                        HistoricalEvolutionDataRepository historicalEvolutionDataRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.historicalEvolutionDataRepository = historicalEvolutionDataRepository;
    }

    @PostMapping("solve")
    public void solveWithEvolutionaryAlgorithm(@RequestBody EvolutionarySolverParameters parameters) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        Class<? extends EvolutionarySolver> solverClass = getSolverClass(parameters.getSolverClass());

        assert solverClass != null;
        List<HistoricalEvolutionData> historicalData = new ArrayList<>();
        historicalData.addAll(handleZeroBlockedPassengerRatio(parameters, instances, solverClass));
        historicalData.addAll(handleGTEZeroBlockedPassengerRatio(parameters, instances, solverClass));

        problemInstanceRepository.saveAll(instances);
        historicalEvolutionDataRepository.saveAll(historicalData);
    }

    private List<HistoricalEvolutionData> handleZeroBlockedPassengerRatio(EvolutionarySolverParameters parameters,
                                                                          List<ProblemInstance> instances,
                                                                          Class<? extends EvolutionarySolver> solverClass) {
        if (solverClass.getName().contains("KnownSolutions")) {
            instances.stream()
                .filter(instance -> instance.getFeatureDescription().getBlockedPassengerRatio() == 0.0)
                .toList()
                .parallelStream()
                .forEach(instance -> {
                    CostComputer costComputer = new CostComputer(instance.getProblem());
                    List<Map<Passenger, Integer>> knownExactSolutions = getExactSolutions(instance);
                    double minKnownCost = knownExactSolutions.stream()
                        .map(solution -> costComputer.computeCost((HashMap<Passenger, Integer>) solution))
                        .min(Double::compareTo)
                        .get();
                    instance.getFeatureDescription().setAlgorithmCost(minKnownCost, solverClass);
                });
            return new ArrayList<>();
        } else {
            return instances.stream()
                .filter(instance -> instance.getFeatureDescription().getBlockedPassengerRatio() == 0.0)
                .toList()
                .parallelStream()
                .map(instance -> solveInstance(parameters, solverClass, instance)).collect(Collectors.toList());
        }
    }

    private List<HistoricalEvolutionData> handleGTEZeroBlockedPassengerRatio(EvolutionarySolverParameters parameters,
                                                                             List<ProblemInstance> instances,
                                                                             Class<? extends EvolutionarySolver> solverClass) {
        return instances.stream()
            .filter(instance -> instance.getFeatureDescription().getBlockedPassengerRatio() > 0.0)
            .toList()
            .parallelStream().map(instance -> solveInstance(parameters, solverClass, instance)).toList();
    }

    private HistoricalEvolutionData solveInstance(EvolutionarySolverParameters parameters, Class<? extends EvolutionarySolver> solverClass, ProblemInstance instance) {
        List<Map<Passenger, Integer>> knownExactSolutions = getExactSolutions(instance);

        try {
            Constructor<? extends EvolutionarySolver> con = solverClass.getConstructor(OneTrainTransferProblem.class, knownExactSolutions.getClass(), SolverConfiguration.class);
            EvolutionarySolver solver = con.newInstance(instance.getProblem(), knownExactSolutions, parameters.getSolverConfiguration());
            SolutionAndHistoryData result = solver.solveAndGenerateHistoricalData();
            CostComputer costComputer = new CostComputer(instance.getProblem());
            double cost = costComputer.computeCost(result.getPassengerMapping());
            instance.getFeatureDescription().setAlgorithmCost(cost, solverClass);

            fillInInstanceData(result.getHistoricalData(), instance, parameters.getSolverConfiguration());
            System.out.println("Finish solving " + instance.getInstanceId());
            return result.getHistoricalData();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }




    //TODO potential future change to be able to give parameters to solver
    private Class<? extends EvolutionarySolver> getSolverClass(String solverName) {
        try {
            return Class.forName(solverName).asSubclass(EvolutionarySolver.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<Passenger, Integer>> getExactSolutions(ProblemInstance instance) {
        int passengerCount = instance.getProblem().getPassengers().size();

        Map<Integer, Passenger> idPassengerHelper = getPassengerOfId(instance);

        List<Map<Integer, Integer>> exactSolutions = instance.getSolverSolutions().values().stream()
            .filter(map -> map.keySet().size() == passengerCount)
            .toList();

        return exactSolutions.stream()
            .map(integerIntegerMap ->
                integerIntegerMap.entrySet().stream()
                    .map(entry -> new Tuple<>(idPassengerHelper.get(entry.getKey()), entry.getValue()))
                    .collect(Collectors.toMap(Tuple::getLeft, Tuple::getRight)))
            .collect(Collectors.toList());
    }

    private Map<Integer, Passenger> getPassengerOfId(ProblemInstance instance) {
        return instance.getProblem().getPassengers().stream()
            .collect(Collectors.toMap(Passenger::getId, passenger -> passenger));
    }

    private void fillInInstanceData(HistoricalEvolutionData data, ProblemInstance instance, SolverConfiguration solverConfiguration) {
        data.setInstanceId(instance.getInstanceId());
        data.setExperimentId(instance.getExperimentId());
        data.setSolverConfiguration(solverConfiguration.getConfigurationAsString());
    }
}
