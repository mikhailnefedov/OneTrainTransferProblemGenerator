package com.github.onetraintransferproblemgenerator.serialization;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.models.Platform;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class InstanceToCSVWriter {

    private static String FILE_PATH = "./metadata.csv";

    public static void writeCSV(List<InstanceFeatureDescription> descriptions) {

        try (Writer writer = new FileWriter(FILE_PATH)) {

            StatefulBeanToCsv<InstanceFeatureDescription> sbc =
                new StatefulBeanToCsvBuilder<InstanceFeatureDescription>(writer)
                    .withQuotechar(' ')
                    .build();

            sbc.write(descriptions);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

}
