package com.github.onetraintransferproblemgenerator;

import com.github.onetraintransferproblemgenerator.generation.SimpleGenerator;
import com.github.onetraintransferproblemgenerator.generation.realistic.RealisticGenerator;
import com.github.onetraintransferproblemgenerator.orchestration.OrchestrationParameters;
import com.github.onetraintransferproblemgenerator.orchestration.SimpleOrchestration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OneTrainTransferProblemGeneratorApplication {

    public static void main(String[] args) {
        //SpringApplication.run(OneTrainTransferProblemGeneratorApplication.class, args);

        OrchestrationParameters parameters = new OrchestrationParameters();
        parameters.setCsvFilePath("./metadata_simple.csv");
        parameters.setInstanceCount(25);
        parameters.setInstanceIdPrefix("simple_");
        parameters.setGenerator(new SimpleGenerator());

        SimpleOrchestration orchestration = new SimpleOrchestration(parameters);

        orchestration.runOrchestration();
    }

}
