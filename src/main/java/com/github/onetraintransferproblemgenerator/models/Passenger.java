package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

@Data
public class Passenger {

    private int id;
    /**
     * station at which passenger arrives
     */
    private int inStation;
    /**
     * station at which passenger alights
     */
    private int outStation;
    /**
     * position at which passenger arrives
     */
    private int inPosition;
    /**
     * target position at target platform where passenger want to leave station/transfer to other train
     */
    private int outPosition;
}
