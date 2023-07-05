package com.github.onetraintransferproblemgenerator.controller.solver;

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

@RestController
@RequestMapping("solver")
public class SolverController {

    private final ProblemInstanceRepository problemInstanceRepository;

    public SolverController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
    }

    @PostMapping("single")
    public void solveWithDeterministicAlgorithm(@RequestBody DeterministicSolverParameters parameters) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        Class<? extends OneTrainTransferSolver> solverClass = getSolverClass(parameters.getSolverClass());
        instances.parallelStream().forEach(instance -> {
            HashMap<Passenger, Integer> resultMapping = solveWithSolver(solverClass, instance);
            CostComputer costComputer = new CostComputer(instance.getProblem());
            double cost = costComputer.computeCost(resultMapping);
            instance.getFeatureDescription().setAlgorithmCost(cost, solverClass);
        });
        problemInstanceRepository.saveAll(instances);
    }

    private Class<? extends OneTrainTransferSolver> getSolverClass(String solverName) {
        try {
            return Class.forName(solverName).asSubclass(OneTrainTransferSolver.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<Passenger, Integer> solveWithSolver(Class<? extends OneTrainTransferSolver> solverClass, ProblemInstance instance) {
        try {
            Constructor<? extends OneTrainTransferSolver> con = solverClass.getConstructor(OneTrainTransferProblem.class);
            OneTrainTransferSolver solver = con.newInstance(instance.getProblem());
            return solver.solve();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
