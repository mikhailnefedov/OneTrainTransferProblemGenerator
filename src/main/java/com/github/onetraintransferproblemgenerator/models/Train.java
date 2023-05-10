package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Train {
    private ArrayList<RailCarriage> railCarriages = new ArrayList<>();
    /**
     * left: station id, right: platform position
     */
    private ArrayList<Tuple<Integer, StationOperation>> stations = new ArrayList<>();
}
