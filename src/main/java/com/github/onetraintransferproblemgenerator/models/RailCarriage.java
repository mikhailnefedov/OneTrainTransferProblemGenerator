package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

@Data
public class RailCarriage {
    private int sequenceNumber;
    private int capacity;

    public int computeCarriagePosition(int trainLength, int position, DirectionOfTravel travelDirection) {
        if (travelDirection.equals(DirectionOfTravel.ascending)) {
            return position + sequenceNumber - 1;
        } else {
            return position + trainLength - sequenceNumber;
        }
    }
}
