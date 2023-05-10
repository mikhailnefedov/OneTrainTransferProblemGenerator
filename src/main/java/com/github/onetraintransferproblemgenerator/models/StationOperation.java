package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

@Data
public class StationOperation {
    private int platformId;
    private int position;
    private DirectionOfTravel travelDirection;
}

