package com.github.onetraintransferproblemgenerator.controller.export;

import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.serialization.CsvExporter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exporting CSV of dataset to project file location
 */
@RestController
@RequestMapping("export")
public class ExportController {

    private final ProblemInstanceRepository problemInstanceRepository;

    public ExportController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
    }

    @GetMapping("csv/{experimentId}")
    void exportExperimentToCsv(@PathVariable String experimentId) {
        List<ProblemInstance> instances = problemInstanceRepository.findAllByExperimentId(experimentId);
        CsvExporter.exportToCsv(instances, experimentId);
    }

}
