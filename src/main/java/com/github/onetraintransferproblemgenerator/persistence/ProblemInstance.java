package com.github.onetraintransferproblemgenerator.persistence;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ProblemInstance {

    private ObjectId id;
    private String instanceId;
    private String experimentId;
    private OneTrainTransferProblem problem;
    private InstanceFeatureDescription featureDescription;

    public ProblemInstance(OneTrainTransferProblem problem, InstanceFeatureDescription description, String experimentId) {
        this.problem = problem;
        featureDescription = description;
        instanceId = featureDescription.getInstanceId();
        this.experimentId = experimentId;
    }
}
