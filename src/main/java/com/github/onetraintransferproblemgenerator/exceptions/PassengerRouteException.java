package com.github.onetraintransferproblemgenerator.exceptions;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;

public class PassengerRouteException extends Exception {

    public PassengerRouteException(OneTrainTransferProblem faultyInstance, Passenger passenger) {
        super(faultyInstance.toString() + ";" + passenger.toString());
    }
}
