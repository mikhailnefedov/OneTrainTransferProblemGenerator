package com.github.onetraintransferproblemgenerator.persistence;

import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    private HashMap<String, Map<Integer, Integer>> solverSolutions;

    public ProblemInstance(OneTrainTransferProblem problem, String experimentId, Class generator, String instanceId) {
        this.problem = problem;
        this.experimentId = experimentId;
        this.source = generator.getSimpleName();
        this.instanceId = instanceId;
        solverSolutions = new HashMap<>();

        featureDescription = new InstanceFeatureDescription();
    }

    public void setSolverSolutions(HashMap<Class<? extends OneTrainTransferSolver>, HashMap<Passenger, Integer>> solutions) {
        solutions.forEach((key, value) -> {
            Map<Integer, Integer> carriageIdOfPassengerId = value.entrySet().stream()
                .map(passengerEntry -> new Tuple<>(passengerEntry.getKey().getId(), passengerEntry.getValue()))
                .collect(Collectors.toMap(Tuple::getLeft, Tuple::getRight));
            solverSolutions.put(key.getSimpleName(), carriageIdOfPassengerId);
        });
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
