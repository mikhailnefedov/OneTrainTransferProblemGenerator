package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrelimResponse {
    private List<PrelimData> featureData;

    public PrelimData getPrelimData(String featureName) {
        return featureData.stream().filter(d -> d.getFeatureName().equals(featureName)).findFirst().get();
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class PrelimData {
    private String featureName;
    private double columnMin;
    private double columnMax;
    private double featureMin;
    private double lambda;
}
