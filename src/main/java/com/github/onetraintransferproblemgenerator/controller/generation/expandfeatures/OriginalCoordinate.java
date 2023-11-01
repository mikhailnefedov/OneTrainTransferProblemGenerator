package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginalCoordinate {
    private double blockedPassengerRatio;
    private double conflictFreePassengerSeatingRatio;
}
