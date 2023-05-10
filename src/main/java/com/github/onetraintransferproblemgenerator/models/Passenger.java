package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

@Data
public class Passenger {
    /**
     * station at which passenger arrives
     */
    private int inStation;
    /**
     * station at which passenger alights
     */
    private int outStation;
    /**
     * platform at which passenger arrives
     */
    private int inPlatform = 1;
    /**
     * platform at which passenger alights
     */
    private int outPlatform = 1;
    /**
     * position at which passenger arrives
     */
    private int inPosition;
}
