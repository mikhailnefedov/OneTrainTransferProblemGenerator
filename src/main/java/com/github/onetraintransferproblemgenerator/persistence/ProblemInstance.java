package com.github.onetraintransferproblemgenerator.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public ProblemInstance(OneTrainTransferProblem problem, String experimentId, Class generator, String instanceId) {
        this.problem = problem;
        this.experimentId = experimentId;
        this.generatorName = generator.getName();
        this.instanceId = instanceId;

        featureDescription = new InstanceFeatureDescription();
        featureDescription.setInstanceId(instanceId);
        featureDescription.setSource(generator.getSimpleName());
    }

    public ProblemInstance deepClone() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ProblemInstance copy = objectMapper.readValue(objectMapper.writeValueAsString(this), ProblemInstance.class);
            copy.getFeatureDescription().resetAlgorithmCosts();
            return copy;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
