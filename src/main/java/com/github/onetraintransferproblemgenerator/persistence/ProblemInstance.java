package com.github.onetraintransferproblemgenerator.persistence;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("probleminstances")
public class ProblemInstance {

    @Id
    private ObjectId id;
    private String experimentId;
    private String instanceId;
    private String generatorName;
    private OneTrainTransferProblem problem;
    private InstanceFeatureDescription featureDescription;

    public ProblemInstance(OneTrainTransferProblem problem, String experimentId, String generatorName, String instanceId) {
        this.problem = problem;
        this.experimentId = experimentId;
        this.generatorName = generatorName;
        this.instanceId = instanceId;

        featureDescription = new InstanceFeatureDescription();
        featureDescription.setInstanceId(instanceId);
    }
}
