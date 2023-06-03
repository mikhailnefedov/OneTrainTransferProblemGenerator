package com.github.onetraintransferproblemgenerator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationTuple {
    private Integer stationId;
    private StationOperation stationOperation;
}
