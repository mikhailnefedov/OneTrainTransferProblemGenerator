package com.github.onetraintransferproblemgenerator.generation;

import lombok.Data;

@Data
public abstract class BaseGenerator implements OneTrainTransferProblemGenerator {

    private final int INSTANCE_COUNT = 150;
    protected int MIN_STATION_COUNT = 3;
    protected int MAX_STATION_COUNT = 10;
    protected double MIN_CONGESTION = 0.05;
    protected double MAX_CONGESTION = 1.00;
    protected double CONGESTION_INCREMENT = 0.05;
    /**
     * Configuration for SimpleGenerator
     */
    protected int MIN_RAIL_CARRIAGES = 1;
    protected int MAX_RAIL_CARRIAGES = 10;
    protected int MAX_RAIL_CARRIAGE_CAPACITY = 80;
    protected int POSITION_COUNT = 14;
}
