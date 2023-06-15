package com.github.onetraintransferproblemgenerator.controller.evolution;

import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceCoordsPair {
    private ProblemInstance instance;
    private List<Double> coords;
}
