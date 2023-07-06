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
    private String source;
    private OneTrainTransferProblem problem;
    private InstanceFeatureDescription featureDescription;

    public ProblemInstance(OneTrainTransferProblem problem, String experimentId, Class generator, String instanceId) {
        this.problem = problem;
        this.experimentId = experimentId;
        this.source = generator.getSimpleName();
        this.instanceId = instanceId;

        featureDescription = new InstanceFeatureDescription();
    }

    public ProblemInstance deepClone() {
        ProblemInstance copy = new ProblemInstance();
        copy.setId(id);
        copy.setExperimentId(experimentId);
        copy.setProblem(problem.deepCopy());
        copy.setFeatureDescription(featureDescription);
        return copy;
    }
}
