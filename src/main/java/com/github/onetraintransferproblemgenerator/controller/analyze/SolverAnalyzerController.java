package com.github.onetraintransferproblemgenerator.controller.analyze;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("solveranalyzer")
public class SolverAnalyzerController {

    final ProblemInstanceRepository problemInstanceRepository;
    private final double PERFORMANCE_THRESHOLD = 1.05;

    public SolverAnalyzerController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
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
}
