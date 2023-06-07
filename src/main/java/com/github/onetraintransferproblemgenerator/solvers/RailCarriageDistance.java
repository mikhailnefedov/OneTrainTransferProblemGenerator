package com.github.onetraintransferproblemgenerator.solvers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class RailCarriageDistance {
    private int railCarriageId;
    private int combinedDistances;
    private int inDistance;
    private int outDistance;

    public RailCarriageDistance(int railCarriageId, int inDistance, int outDistance) {
        combinedDistances = inDistance + outDistance;
        this.railCarriageId = railCarriageId;
        this.inDistance = inDistance;
        this.outDistance = outDistance;
    }
}
