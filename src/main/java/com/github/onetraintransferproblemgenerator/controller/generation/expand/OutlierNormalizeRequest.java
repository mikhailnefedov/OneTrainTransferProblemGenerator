package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutlierNormalizeRequest {
    private List<ProblemInstance> instances;
    private List<String> featureNames;
}
