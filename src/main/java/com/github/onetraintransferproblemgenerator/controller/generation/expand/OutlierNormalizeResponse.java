package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutlierNormalizeResponse {
    private List<OutlierNormalizeData> featureData;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class OutlierNormalizeData {
    private double columnMin;
    private double columnMax;
    private double featureMin;
    private double lambda;
}
