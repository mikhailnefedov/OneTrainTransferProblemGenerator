package com.github.onetraintransferproblemgenerator.controller.generation.expand;

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
@RequestMapping("expandinstancespace")
public class ExpandInstanceSpaceGenerationController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";

    public ExpandInstanceSpaceGenerationController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("expand")
    void generateInstances(@RequestBody ExpandInstanceSpaceParameters parameters) {
        System.out.println(getOutlierNormalizeData(parameters));


    }

    private OutlierNormalizeResponse getOutlierNormalizeData(ExpandInstanceSpaceParameters parameters) {
        List<ProblemInstance> problemInstances =
                problemInstanceRepository.findAllByExperimentId(parameters.getExperimentId());

        OutlierNormalizeRequest data = new OutlierNormalizeRequest();
        data.setInstances(problemInstances);
        data.setFeatureNames(parameters.getFeatureNames());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OutlierNormalizeRequest> request = new HttpEntity<>(data, headers);
        return restTemplate.postForEntity(PYTHON_BACKEND_URL + "/outliernormalizedata", request, OutlierNormalizeResponse.class)
                .getBody();
    }
}
