package com.github.onetraintransferproblemgenerator.exceptions;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;

public class NotEnoughTrainCapacityException extends Exception {

    public NotEnoughTrainCapacityException(OneTrainTransferProblem faultyInstance) {
        super(faultyInstance.toString());
    }
}
