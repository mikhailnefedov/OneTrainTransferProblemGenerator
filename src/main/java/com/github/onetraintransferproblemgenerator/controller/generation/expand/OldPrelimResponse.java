package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OldPrelimResponse {
    private List<OldPrelimData> featureData;

    public OldPrelimData getPrelimData(String featureName) {
        return featureData.stream().filter(d -> d.getFeatureName().equals(featureName)).findFirst().get();
    }

}

/**
 * Old in this case means --> Changed client side of prelim computation and this is the old client code of it
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class OldPrelimData {
    private String featureName;
    private double columnMin;
    private double columnMax;
    private double featureMin;
    private double lambda;
}
