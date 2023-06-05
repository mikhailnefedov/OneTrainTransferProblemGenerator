package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.serialization.CsvName;
import lombok.Data;

@Data
public class InstanceFeatureDescription {
    @CsvName(column = "Instances")
    private String instanceId;
    @CsvName(column = "Source")
    private String source;
    @CsvName(column = "feature_stationCount")
    private int stationCount;
    @CsvName(column = "feature_directionChangeCount")
    private int directionChangeCount;
    @CsvName(column = "feature_passengerCount")
    private int passengerCount;
    @CsvName(column = "feature_averageRailCarriageCapacity")
    private double averageRailCarriageCapacity;
    @CsvName(column = "feature_averageCongestion")
    private double averageCongestion;
    @CsvName(column = "feature_maxCongestion")
    private double maxCongestion;
    @CsvName(column = "algo_firstAvailableCarriage")
    private double firstAvailableCarriageCost;
    @CsvName(column = "algo_greedy")
    private double greedyCost;
}
