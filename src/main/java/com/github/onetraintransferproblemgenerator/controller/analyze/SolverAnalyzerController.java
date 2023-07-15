package com.github.onetraintransferproblemgenerator.controller.analyze;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.HistoricalEvolutionDataRepository;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.HistoricalEvolutionData;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("solveranalyzer")
public class SolverAnalyzerController {

    final ProblemInstanceRepository problemInstanceRepository;
    final HistoricalEvolutionDataRepository historicalEvolutionDataRepository;
    private final double PERFORMANCE_THRESHOLD = 1.05;

    public SolverAnalyzerController(ProblemInstanceRepository problemInstanceRepository,
                                    HistoricalEvolutionDataRepository historicalEvolutionDataRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.historicalEvolutionDataRepository = historicalEvolutionDataRepository;
    }

    @GetMapping("analyze/{experimentId}")
    public ResponseEntity analyzeExperimentAlgorithmCosts(@PathVariable String experimentId) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(experimentId);

        HashMap<String, Integer> performanceTracker = initializePerformanceTracker();

        instances.stream()
            .map(instance -> instance.getFeatureDescription().getAlgorithmCosts())
            .forEach(algorithmCosts -> {
                double bestCost = algorithmCosts.stream()
                    .min(Comparator.comparingDouble(Tuple::getRight))
                    .get().getRight();

                algorithmCosts.forEach(algorithmCost -> {
                    if (algorithmCost.getRight() <= bestCost * PERFORMANCE_THRESHOLD) {
                        int performanceCount = performanceTracker.get(algorithmCost.getLeft());
                        performanceTracker.put(algorithmCost.getLeft(), performanceCount + 1);
                    }
                });
            });

        return ResponseEntity.ok(performanceTracker);
    }

    private HashMap<String, Integer> initializePerformanceTracker() {
        HashMap<String, Integer> map = new HashMap<>();
        List<String> algorithms = InstanceFeatureDescription.getAlgorithmNames();
        for (String algorithm : algorithms) {
            map.put(algorithm, 0);
        }
        return map;
    }

    @GetMapping("fitnessimprovement/{experimentId}")
    public ResponseEntity analyzeFitnessImprovementsOfEvolutionaryAlgorithms(@PathVariable String experimentId) {
        List<HistoricalEvolutionData> evolutionData = historicalEvolutionDataRepository.findAllByExperimentId(experimentId);

        Map<String, List<HistoricalEvolutionData>> dataGroupedByInstances = evolutionData.stream()
            .collect(Collectors.groupingBy(HistoricalEvolutionData::getInstanceId));

        HashMap<String, List<List<Integer>>> fitnessImprovementsBySolver = initializeFitnessImprovementsBySolverMap(evolutionData);
        dataGroupedByInstances.forEach((key, value) -> {
            for (HistoricalEvolutionData data : value) {
                String solverName = data.getSolverName();
                List<Integer> durations = getFitnessImprovementDurations(data.getCostData());
                fitnessImprovementsBySolver.get(solverName).add(durations);
            }
        });

        return ResponseEntity.ok(fitnessImprovementsBySolver);
    }

    private HashMap<String, List<List<Integer>>> initializeFitnessImprovementsBySolverMap(List<HistoricalEvolutionData> evolutionData) {
        List<String> solverNames = evolutionData.stream().map(HistoricalEvolutionData::getSolverName).toList();

        HashMap<String, List<List<Integer>>> fitnessImprovementsBySolver = new HashMap<>();
        for (String solverName : solverNames) {
            fitnessImprovementsBySolver.put(solverName, new ArrayList<>());
        }
        return fitnessImprovementsBySolver;
    }

    private List<Integer> getFitnessImprovementDurations(List<Double> costs) {
        List<Integer> durations = new ArrayList<>();

        double tmp = costs.get(0);
        int duration = 0;
        for (double cost : costs) {
            if (cost != tmp) {
                tmp = cost;
                durations.add(duration);
                duration = 1;
            } else {
                duration++;
            }
        }
        return durations;
    }
}
