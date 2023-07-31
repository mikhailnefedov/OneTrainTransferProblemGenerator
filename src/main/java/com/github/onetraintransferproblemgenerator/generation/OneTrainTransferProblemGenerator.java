package com.github.onetraintransferproblemgenerator.generation;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;

public interface OneTrainTransferProblemGenerator {

    int MIN_STATION_COUNT = 3;
    int MAX_STATION_COUNT = 10;

    double MIN_CONGESTION = 0.05;
    double MAX_CONGESTION = 1.0;
    double CONGESTION_INCREMENT = 0.05;

    /**
     * Configuration for SimpleGenerator
     */
    int MIN_RAIL_CARRIAGES = 1;
    int MAX_RAIL_CARRIAGES = 8;
    int MAX_RAIL_CARRIAGE_CAPACITY = 100;
    int POSITION_COUNT = 14;

    OneTrainTransferProblem generate();
}
