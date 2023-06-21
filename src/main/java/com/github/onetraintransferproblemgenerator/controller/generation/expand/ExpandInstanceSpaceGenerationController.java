package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import org.ejml.simple.SimpleMatrix;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.BiFunction;

@RestController
@RequestMapping("expandinstancespace")
public class ExpandInstanceSpaceGenerationController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";
    private PrelimUtils prelimUtils;

    public ExpandInstanceSpaceGenerationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.restTemplate = new RestTemplate();
        prelimUtils = new PrelimUtils();
    }

    @PostMapping("expand")
    void generateInstances(@RequestBody ExpandInstanceSpaceParameters parameters) {
        initializeKnownInstancesWithTheirCoordinates(parameters);
    }

    private List<Tuple<ProblemInstance, SimpleMatrix>> initializeKnownInstancesWithTheirCoordinates(ExpandInstanceSpaceParameters parameters) {
        List<ProblemInstance> problemInstances =
                problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        PrelimResponse prelimData = getPrelimData(problemInstances, parameters);

        List<List<Tuple<String, Double>>> featureVectors = problemInstances.stream()
                .map(instance -> instance.getFeatureDescription().getFeatureVector(parameters.getFeatureNames()))
                .toList();

        List<List<Double>> transformedFeatureVectors = prelimUtils.doPrelim(featureVectors, prelimData);
        List<SimpleMatrix> coords = ProjectionUtils.projectFeatureVector(transformedFeatureVectors, parameters.getTransposedProjectionMatrix());

        return StreamUtils
                .zip(problemInstances.stream(), coords.stream(), (BiFunction<ProblemInstance, SimpleMatrix, Tuple<ProblemInstance, SimpleMatrix>>) Tuple::new)
                .toList();
    }

    private PrelimResponse getPrelimData(List<ProblemInstance> problemInstances, ExpandInstanceSpaceParameters parameters) {
        PrelimRequest data = new PrelimRequest();
        data.setInstances(problemInstances);
        data.setFeatureNames(parameters.getFeatureNames());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PrelimRequest> request = new HttpEntity<>(data, headers);
        return restTemplate.postForEntity(PYTHON_BACKEND_URL + "/prelimdata", request, PrelimResponse.class)
                .getBody();
    }

}
