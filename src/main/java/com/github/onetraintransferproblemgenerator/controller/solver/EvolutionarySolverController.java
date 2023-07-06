package com.github.onetraintransferproblemgenerator.controller.solver;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.solvers.CostComputer;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("evolutionary")
public class EvolutionarySolverController {

    private final ProblemInstanceRepository problemInstanceRepository;

    public EvolutionarySolverController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
    }

    @PostMapping("solve")
    public void solveWithEvolutionaryAlgorithm(@RequestBody EvolutionarySolverParameters parameters) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        Class<? extends OneTrainTransferSolver> solverClass = getSolverClass(parameters.getSolverClass());
        instances.parallelStream().forEach(instance -> {
            List<Map<Passenger, Integer>> knownExactSolutions = getExactSolutions(instance);

            try {
                Constructor<? extends OneTrainTransferSolver> con = solverClass.getConstructor(OneTrainTransferProblem.class, knownExactSolutions.getClass());
                OneTrainTransferSolver solver = con.newInstance(instance.getProblem(), knownExactSolutions);
                HashMap<Passenger, Integer> resultMapping = solver.solve();
                CostComputer costComputer = new CostComputer(instance.getProblem());
                double cost = costComputer.computeCost(resultMapping);
                instance.getFeatureDescription().setAlgorithmCost(cost, solverClass);
                System.out.println("Finish solving " + instance.getInstanceId());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        problemInstanceRepository.saveAll(instances);
    }


    //TODO potential future change to be able to give parameters to solver
    private Class<? extends OneTrainTransferSolver> getSolverClass(String solverName) {
        try {
            return Class.forName(solverName).asSubclass(OneTrainTransferSolver.class);
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
}
