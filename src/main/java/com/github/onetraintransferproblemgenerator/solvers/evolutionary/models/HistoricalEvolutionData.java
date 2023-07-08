package com.github.onetraintransferproblemgenerator.solvers.evolutionary.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class HistoricalEvolutionData {
    @Id
    private ObjectId id;
    private String experimentId;
    private String instanceId;
    private String solverName;
    private List<Double> costData = new ArrayList<>();

    public void addBestCost(double cost) {
        costData.add(cost);
    }
}
