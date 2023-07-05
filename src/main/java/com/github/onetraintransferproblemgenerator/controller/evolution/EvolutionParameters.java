package com.github.onetraintransferproblemgenerator.controller.evolution;

import com.github.onetraintransferproblemgenerator.controller.visualization.VisualizationParameters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionParameters extends VisualizationParameters {
    private List<Double> targetPoint;
    private int generationCount;
    private int populationSize;
}
