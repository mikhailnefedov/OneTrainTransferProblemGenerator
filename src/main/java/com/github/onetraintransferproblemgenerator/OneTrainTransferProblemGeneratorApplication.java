package com.github.onetraintransferproblemgenerator;

import com.github.onetraintransferproblemgenerator.generation.realistic.RealisticGenerator;
import com.github.onetraintransferproblemgenerator.orchestration.OrchestrationParameters;
import com.github.onetraintransferproblemgenerator.orchestration.SimpleOrchestration;
import com.github.onetraintransferproblemgenerator.solvers.FirstAvailableCarriageSolver;
import com.github.onetraintransferproblemgenerator.solvers.GreedySolver;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class OneTrainTransferProblemGeneratorApplication {

    public static void main(String[] args) {
        //SpringApplication.run(OneTrainTransferProblemGeneratorApplication.class, args);

        OrchestrationParameters parameters = new OrchestrationParameters();
        parameters.setCsvFilePath("./metadata.csv");
        parameters.setInstanceCount(10);
        parameters.setInstanceIdPrefix("realistic_");
        parameters.setGenerator(new RealisticGenerator());

        List<Class<? extends OneTrainTransferSolver>> solvers = new ArrayList<>();
        solvers.add(FirstAvailableCarriageSolver.class);
        solvers.add(GreedySolver.class);
        parameters.setSolvers(solvers);

        SimpleOrchestration orchestration = new SimpleOrchestration(parameters);

        orchestration.runOrchestration();
    }

}
