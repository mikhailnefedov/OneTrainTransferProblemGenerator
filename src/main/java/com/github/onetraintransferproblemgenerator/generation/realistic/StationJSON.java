package com.github.onetraintransferproblemgenerator.generation.realistic;

import lombok.Data;

import java.util.List;

@Data
class StationJSON {
    private String stationName;
    private int stationClass;
    private boolean railhead;
    private int numberOfPositions;
    private List<Integer> accessPositions;

    public StationJSON() {

    }
}
