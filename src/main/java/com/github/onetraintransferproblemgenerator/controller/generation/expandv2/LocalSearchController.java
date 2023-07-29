package com.github.onetraintransferproblemgenerator.controller.generation.expandv2;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.PrelimRequest;
import com.github.onetraintransferproblemgenerator.helpers.MathUtils;
import com.github.onetraintransferproblemgenerator.persistence.PrelimInformationRepository;
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

/**
 * Other methodology compared to .expand package
 */
@RestController
@RequestMapping("localsearch")
public class LocalSearchController {

    private final ProblemInstanceRepository problemInstanceRepository;
    private final PrelimInformationRepository prelimInformationRepository;
    private final RestTemplate restTemplate;
    private final String PYTHON_BACKEND_URL = "http://localhost:5000";

    public LocalSearchController(ProblemInstanceRepository problemInstanceRepository,
                                 PrelimInformationRepository prelimInformationRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
        this.prelimInformationRepository = prelimInformationRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("init")
    void initExpandInstanceSpace(@RequestBody LocalSearchInitialization initParams) {
        List<ProblemInstance> problemInstances =
            problemInstanceRepository.findAllByExperimentId(initParams.getExperimentId());
        PrelimResponse prelimResponse = getPrelimData(problemInstances, initParams);
        PrelimInformation prelimInformation = new PrelimInformation(initParams.getExperimentId(), prelimResponse);
        setMeanAndDeviations(problemInstances, prelimInformation);
        prelimInformationRepository.save(prelimInformation);
    }

    private PrelimResponse getPrelimData(List<ProblemInstance> problemInstances, LocalSearchInitialization initParams) {
        PrelimRequest data = new PrelimRequest();
        data.setInstances(problemInstances);
        data.setFeatureNames(initParams.getFeatureNames());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PrelimRequest> request = new HttpEntity<>(data, headers);
        return restTemplate.postForEntity(PYTHON_BACKEND_URL + "/prelimdata", request, PrelimResponse.class)
            .getBody();
    }

    private void setMeanAndDeviations(List<ProblemInstance> problemInstances, PrelimInformation prelimInformation) {
        prelimInformation.getPrelimDataOfFeatures().entrySet().forEach(entry -> {
            String featureName = entry.getKey();

            List<Double> values = problemInstances.stream()
                .map(instance -> instance.getFeatureDescription().getValueByFeatureName(featureName))
                .toList();

            double mean = MathUtils.computeMean(values);
            double stdDeviation = MathUtils.computeStandardDeviation(values, mean);

            entry.getValue().setMean(mean);
            entry.getValue().setStdDeviation(stdDeviation);
        });
    }

}
