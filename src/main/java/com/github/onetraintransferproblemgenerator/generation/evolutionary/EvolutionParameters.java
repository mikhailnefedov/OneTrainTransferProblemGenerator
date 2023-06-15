package com.github.onetraintransferproblemgenerator.generation.evolutionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionParameters {
    private int generationCount;
    private int populationCount;
    private double crossoverRate;
    private double mutationRate;
}
