package com.github.onetraintransferproblemgenerator.controller.generation.expandv2;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PrelimInformation {

    @Id
    private ObjectId id;
    private String experimentId;
    private Map<String, PrelimData> prelimDataOfFeatures;

    public PrelimInformation(String experimentId, PrelimResponse prelimResponse) {
        this.experimentId = experimentId;
        prelimDataOfFeatures =
            prelimResponse.getFeatureData().stream().collect(Collectors.toMap(PrelimData::getFeatureName, prelimData -> prelimData));
    }
}

@Data
class PrelimData {
    private String featureName;
    private double columnMin;
    private double columnMax;
    private double featureMin;
    private double lambda;
    private double mean;
    private double stdDeviation;
}
