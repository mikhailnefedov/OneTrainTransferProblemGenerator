package com.github.onetraintransferproblemgenerator.generation;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import lombok.Data;

import java.util.List;

@Data
public abstract class BaseGenerator implements OneTrainTransferProblemGenerator {

    protected int MIN_STATION_COUNT = 3;
    protected int MAX_STATION_COUNT = 6;

    protected double MIN_CONGESTION = 0.05;
    protected double MAX_CONGESTION = 1.05;
    protected double CONGESTION_INCREMENT = 0.05;

    /**
     * Configuration for SimpleGenerator
     */
    protected int MIN_RAIL_CARRIAGES = 1;
    protected int MAX_RAIL_CARRIAGES = 4;
    protected int MAX_RAIL_CARRIAGE_CAPACITY = 20;
    protected int POSITION_COUNT = 14;

    private final int INSTANCE_COUNT = 150;
}
