package com.github.onetraintransferproblemgenerator.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class OneTrainTransferProblem {
    private Train train;
    private List<Passenger> passengers;
}
