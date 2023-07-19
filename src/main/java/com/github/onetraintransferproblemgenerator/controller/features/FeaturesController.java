package com.github.onetraintransferproblemgenerator.controller.features;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("features")
public class FeaturesController {

    private final ProblemInstanceRepository problemInstanceRepository;

    public FeaturesController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
    }

    @PostMapping("extract/{experimentId}")
    public void solveWithDeterministicAlgorithm(@PathVariable String experimentId) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(experimentId);

        for (ProblemInstance instance : instances) {
            FeatureExtractor.extract(instance.getFeatureDescription(), instance.getProblem());
        }

        problemInstanceRepository.saveAll(instances);
    }
}
