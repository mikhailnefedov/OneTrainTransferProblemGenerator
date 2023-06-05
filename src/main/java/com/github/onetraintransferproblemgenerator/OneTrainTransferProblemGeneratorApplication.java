package com.github.onetraintransferproblemgenerator;

import com.github.onetraintransferproblemgenerator.generation.realistic.RealisticGenerator;
import com.github.onetraintransferproblemgenerator.orchestration.OrchestrationParameters;
import com.github.onetraintransferproblemgenerator.orchestration.SimpleOrchestration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OneTrainTransferProblemGeneratorApplication {

    public static void main(String[] args) {
        //SpringApplication.run(OneTrainTransferProblemGeneratorApplication.class, args);

        OrchestrationParameters parameters = new OrchestrationParameters();
        parameters.setCsvFilePath("./metadata.csv");
        parameters.setInstanceCount(50);
        parameters.setGenerator(new RealisticGenerator());

        SimpleOrchestration orchestration = new SimpleOrchestration(parameters);

        orchestration.runOrchestration();
    }

}
