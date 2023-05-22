package com.github.onetraintransferproblemgenerator;

import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.generation.SimpleGenerator;
import com.github.onetraintransferproblemgenerator.orchestration.SimpleOrchestration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OneTrainTransferProblemGeneratorApplication {

    public static void main(String[] args) {
        //SpringApplication.run(OneTrainTransferProblemGeneratorApplication.class, args);

        OneTrainTransferProblemGenerator problemGenerator = new SimpleGenerator();

        SimpleOrchestration orchestration = new SimpleOrchestration(problemGenerator);

        orchestration.runOrchestration();
    }



}
