package com.github.onetraintransferproblemgenerator.controller.evolution;

import com.github.onetraintransferproblemgenerator.controller.visualization.VisualizationData;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("evolution")
public class EvolutionController {
    final
    ProblemInstanceRepository problemInstanceRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";


    public EvolutionController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("evolutionaryalgorithm")
    void generateNewInstancesThroughEvolution(@RequestBody EvolutionParameters parameters) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        List<List<Double>> coordinates = getInstanceCoordinates(parameters, instances).getCoordinates();
        List<Tuple<ProblemInstance, List<Double>>> instanceCoordsTuples = zipInstancesAndCoordinates(instances, coordinates);

        List<Tuple<ProblemInstance, Double>> initialPopulation =
                getNearestInstancesToTargetPoint(instanceCoordsTuples, parameters.getTargetPoint(), parameters.getPopulationSize());

        for (int i = 0; i < parameters.getGenerationCount(); i++) {

        }
    }

    private InstanceCoordsResponse getInstanceCoordinates(EvolutionParameters parameters, List<ProblemInstance> instances) {
        VisualizationData data = VisualizationData.builder()
                .instances(instances)
                .transposedProjectionMatrix(parameters.getTransposedProjectionMatrix())
                .featureNames(parameters.getFeatureNames())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VisualizationData> request = new HttpEntity<>(data, headers);
        ResponseEntity<InstanceCoordsResponse> response = restTemplate.postForEntity(PYTHON_BACKEND_URL + "/instancecoords", request, InstanceCoordsResponse.class);

        return response.getBody();
    }

    private List<Tuple<ProblemInstance, List<Double>>> zipInstancesAndCoordinates(List<ProblemInstance> instances, List<List<Double>> coordinates) {
        return StreamUtils.zip(
                        instances.stream(),
                        coordinates.stream(),
                        Tuple::new)
                .toList();
    }

    private List<Tuple<ProblemInstance, Double>> getNearestInstancesToTargetPoint(List<Tuple<ProblemInstance, List<Double>>> instanceCoordsTuples,
                                                                                        List<Double> targetPoint,
                                                                                        int populationSize) {
        return instanceCoordsTuples.stream()
                .map(instance -> new Tuple<>(instance.getLeft(), computeDistance(instance.getRight(), targetPoint)))
                .sorted(Comparator.comparingDouble(t -> t.getRight()))
                .limit(populationSize)
                .toList();
    }

    private double computeDistance(List<Double> coords1, List<Double> coords2) {
        return Math.abs(coords1.get(0) - coords2.get(0)) + Math.abs(coords1.get(1) - coords2.get(1));
    }
}