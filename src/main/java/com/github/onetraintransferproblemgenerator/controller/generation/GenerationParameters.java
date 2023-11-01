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
    /**
     * id of experiment to identify dataset
     */
    private String experimentId;
    /**
     * should instances be stored in database?
     */
    private boolean storeInstances = false;
    /**
     * generators and their settings
     */
    private Map<String, InstanceGeneration> generators;
    /**
     * these solvers will be used after the generation to solve instances
     */
    private List<String> solvers;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class InstanceGeneration {
    /**
     * instance count to generate
     */
    private int instanceCount;
    /**
     * prefix of instance ids, e.g. "mk_s_" will lead to "mk_s_1","mk_s_2",...
     */
    private String idPrefix;
}
