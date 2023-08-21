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

    public Passenger deepClone() {
        Passenger copy = new Passenger();
        copy.setId(id);
        copy.setInStation(inStation);
        copy.setInPosition(inPosition);
        copy.setOutStation(outStation);
        copy.setOutPosition(outPosition);
        return copy;
    }
}
