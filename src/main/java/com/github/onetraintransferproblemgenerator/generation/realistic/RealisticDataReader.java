package com.github.onetraintransferproblemgenerator.generation.realistic;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RealisticDataReader {

    private static final String STATIONS_FILE_PATH = "data/stations.json";
    private static final String TRAINS_FILE_PATH = "data/trains.json";

    public static List<Station> getStations() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Station> stations = new ArrayList<>();
        try {
            stations = Arrays.asList(objectMapper.readValue(new File(STATIONS_FILE_PATH), Station[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stations;
    }

    public static List<Train> getTrains() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Train> trains = new ArrayList<>();
        try {
            trains = Arrays.asList(objectMapper.readValue(new File(TRAINS_FILE_PATH), Train[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trains;
    }
}
