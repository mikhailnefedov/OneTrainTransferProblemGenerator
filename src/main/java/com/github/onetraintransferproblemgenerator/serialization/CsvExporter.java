package com.github.onetraintransferproblemgenerator.serialization;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;

import java.util.List;
import java.util.stream.Collectors;

public class CsvExporter {

    private static final String FILE_PREFIX = "./metadata_";

    public static void exportToCsv(List<ProblemInstance> instances, String experimentId) {
        List<InstanceFeatureDescription> descriptions = instances.stream()
            .map(ProblemInstance::getFeatureDescription)
            .collect(Collectors.toList());
        String csvFile = FILE_PREFIX + experimentId + ".csv";
        InstanceToCSVWriter.writeToCSV(descriptions, csvFile);
    }
}
