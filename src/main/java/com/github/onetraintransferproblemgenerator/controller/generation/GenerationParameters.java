package com.github.onetraintransferproblemgenerator.controller.generation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerationParameters {
    private String experimentId;
    private boolean storeInstances = false;
    private Map<String, InstanceGeneration> generators;
    private List<String> solvers;
    private String csvFile;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class InstanceGeneration {
    private int instanceCount;
    private String idPrefix;
}
