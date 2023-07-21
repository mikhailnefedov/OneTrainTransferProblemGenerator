package com.github.onetraintransferproblemgenerator.exceptions;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.StationTuple;

public class NotEnoughTrainCapacityException extends Exception {

    public NotEnoughTrainCapacityException(OneTrainTransferProblem faultyInstance, StationTuple station, int faultyCapacity) {
        super(faultyInstance.toString() + ";" + station.toString() + ";" + faultyCapacity);
    }

    public NotEnoughTrainCapacityException(int faultyCapacity) {
        super(String.valueOf(faultyCapacity));
    }
}
