package com.github.onetraintransferproblemgenerator.features;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class InstanceFeatureDescription {
    @CsvBindByName(column = "instances")
    private String instanceId;
    @CsvBindByName(column = "feature_stationCount")
    private int stationCount;
    @CsvBindByName(column = "feature_directionChangeCount")
    private int directionChangeCount;
    @CsvBindByName(column = "feature_passengerCount")
    private int passengerCount;
    @CsvBindByName(column = "feature_averageRailCarriageCapacity")
    private double averageRailCarriageCapacity;
    @CsvBindByName(column = "feature_averageCongestion")
    private double averageCongestion;
    @CsvBindByName(column = "feature_maxCongestion")
    private double maxCongestion;
    @CsvBindByName(column = "algo_firstAvailableCarriage")
    private double firstAvailableCarriageCost;
    @CsvBindByName(column = "algo_greedy")
    private double greedyCost;
}
