package com.github.onetraintransferproblemgenerator.solvers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class RailCarriageDistance {
    private int railCarriageId;
    private double cost;
    private int inDistance;
    private int outDistance;

    public RailCarriageDistance(int railCarriageId, int inDistance, int outDistance) {
        cost = Math.pow(inDistance, 2) + Math.pow(outDistance, 2);
        this.railCarriageId = railCarriageId;
        this.inDistance = inDistance;
        this.outDistance = outDistance;
    }
}
