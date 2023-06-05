package com.github.onetraintransferproblemgenerator.serialization;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class InstanceToCSVWriter {

    private static final String SEPERATOR = ",";

    public static void writeToCSV(List<InstanceFeatureDescription> descriptions, String filePath) {
        File outputFile = new File(filePath);
        try (FileWriter writer = new FileWriter(outputFile)) {
            String header = createHeader();
            writer.write(header);

            descriptions.stream()
                .map(InstanceToCSVWriter::createRow)
                .forEach(d -> {
                    try {
                        writer.write(d);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createHeader() {
        List<Field> declaredFields = List.of(InstanceFeatureDescription.class.getDeclaredFields());
        return declaredFields.stream()
            .map(f -> f.getDeclaredAnnotationsByType(CsvName.class)[0].column())
            .collect(Collectors.joining(SEPERATOR)) + "\n";
    }

    private static String createRow(InstanceFeatureDescription description) {
        List<Field> declaredFields = List.of(InstanceFeatureDescription.class.getDeclaredFields());
        String row = declaredFields.stream()
            .peek(f -> f.setAccessible(true))
            .map(f -> {
                try {
                    return f.get(description).toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return "";
            })
            .collect(Collectors.joining(SEPERATOR)) + "\n";
        declaredFields.forEach(f -> f.setAccessible(false));
        return row;
    }

}
