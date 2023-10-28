package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpandFeaturesProblemInstance {

    @Id
    private ObjectId id;
    private String experimentId;
    private String instanceId;
    private String source;
    private OneTrainTransferProblem problem;
    private InstanceFeatureDescription featureDescription;

    public ProblemInstance convertToProblemInstance() {
        ProblemInstance instance = new ProblemInstance();
        instance.setExperimentId(experimentId);
        instance.setInstanceId(instanceId);
        instance.setSource(source);
        instance.setProblem(problem);
        instance.setFeatureDescription(featureDescription);
        return instance;
    }
}


