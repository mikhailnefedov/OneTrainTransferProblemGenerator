package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class ConflictEvolutionData {
    @Id
    private ObjectId id;
    private String experimentId;
    private String instanceId;
    private List<List<ConflictCoordinate>> coordinateHistories = new ArrayList<>();
    private List<ConflictCoordinate> startGeneration = new ArrayList<>();
    private ConflictCoordinate startCoordinates;


    public ConflictEvolutionData(String experimentId, String instanceId) {
        this.experimentId = experimentId;
        this.instanceId = instanceId;
    }

    public void initNewCoordinateHistory() {
        coordinateHistories.add(new ArrayList<>());
    }

    public void addCoordinate(ConflictEvolutionIndividual individual) {
        coordinateHistories.get(coordinateHistories.size() - 1).add(ConflictCoordinate.convertFromIndividual(individual));
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ConflictCoordinate {
    private double blockedPassengerRatio;
    private double conflictFreePassengerSeatingRatio;

    public static ConflictCoordinate convertFromIndividual(ConflictEvolutionIndividual individual) {
        ConflictCoordinate coordinate = new ConflictCoordinate();
        coordinate.setBlockedPassengerRatio(individual.getProblemInstance().getFeatureDescription().getBlockedPassengerRatio());
        coordinate.setConflictFreePassengerSeatingRatio(individual.getProblemInstance().getFeatureDescription().getConflictFreePassengerSeatingRatio());
        return coordinate;
    }
}
