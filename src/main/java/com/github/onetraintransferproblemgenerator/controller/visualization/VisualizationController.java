package com.github.onetraintransferproblemgenerator.controller.visualization;

import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("visualization")
public class VisualizationController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";

    public VisualizationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("instancesbysource")
    void visualizeInstancesBySource(@RequestBody VisualizationParameters parameters) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        VisualizationData data = VisualizationData.builder()
                .instances(instances)
                .transposedProjectionMatrix(parameters.getTransposedProjectionMatrix())
                .featureNames(parameters.getFeatureNames())
                .axisRangeX(parameters.getAxisRangeX())
                .axisRangeY(parameters.getAxisRangeY())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VisualizationData> request = new HttpEntity<>(data, headers);
        restTemplate.postForEntity(PYTHON_BACKEND_URL + "/visualizationbysource", request, String.class);
    }

    @PostMapping("instancesbystationcount")
    void visualizeInstancesByStationCount(@RequestBody VisualizationParameters parameters) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());
        VisualizationData data = VisualizationData.builder()
                .instances(instances)
                .transposedProjectionMatrix(parameters.getTransposedProjectionMatrix())
                .featureNames(parameters.getFeatureNames())
                .axisRangeX(parameters.getAxisRangeX())
                .axisRangeY(parameters.getAxisRangeY())
                .stationCount(parameters.getStationCount())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VisualizationData> request = new HttpEntity<>(data, headers);
        restTemplate.postForEntity(PYTHON_BACKEND_URL + "/visualizationbystationcount", request, String.class);
    }

}
