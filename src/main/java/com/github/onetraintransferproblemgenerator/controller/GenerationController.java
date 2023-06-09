package com.github.onetraintransferproblemgenerator.controller;

import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.validation.InstanceValidator;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("generation")
public class GenerationController {

    private String experimentId;

    public GenerationController() {

    }

    @PostMapping("generateinstances")
    void generateInstances(@RequestBody GenerationParameters generationParameters) {
        experimentId = generationParameters.getExperimentId();
        List<ProblemInstance> instances = generateInstances(generationParameters.getGenerators());
        System.out.println(generationParameters);
        System.out.println(instances);
    }

    private List<ProblemInstance> generateInstances(Map<String, InstanceGeneration> generators) {
        List<ProblemInstance> problemInstances = new ArrayList<>();
        for (String generatorName : generators.keySet()) {
            try {
                OneTrainTransferProblemGenerator generator = createGenerator(generatorName);
                InstanceGeneration generatorParameter = generators.get(generatorName);
                for (int i = 1; i <= generatorParameter.getInstanceCount(); i++) {
                    ProblemInstance instance = generateInstance(generator, String.format("%s%d",generatorParameter.getIdPrefix(), i));
                    problemInstances.add(instance);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return problemInstances;
    }

    private OneTrainTransferProblemGenerator createGenerator(String generatorName) throws ClassNotFoundException {
        try {
            Class<? extends OneTrainTransferProblemGenerator> generatorClass =
                    Class.forName(generatorName).asSubclass(OneTrainTransferProblemGenerator.class);
            Constructor<? extends OneTrainTransferProblemGenerator> con = generatorClass.getConstructor();
            return con.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new ClassNotFoundException();
    }

    private ProblemInstance generateInstance(OneTrainTransferProblemGenerator generator, String instanceId) {
        OneTrainTransferProblem instance = generator.generate();
        validateInstance(instance);
        ProblemInstance problemInstance = new ProblemInstance(instance, experimentId, instanceId);
        return problemInstance;
    }

    @SneakyThrows
    private void validateInstance(OneTrainTransferProblem instance) {
        InstanceValidator.validateInstance(instance);
    }


}
