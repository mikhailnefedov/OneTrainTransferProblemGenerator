package com.github.onetraintransferproblemgenerator;

import com.github.onetraintransferproblemgenerator.generation.realistic.RealisticGenerator;
import com.github.onetraintransferproblemgenerator.generation.simple.SimpleGenerator;
import com.github.onetraintransferproblemgenerator.orchestration.OrchestrationParameters;
import com.github.onetraintransferproblemgenerator.orchestration.SimpleOrchestration;
import com.github.onetraintransferproblemgenerator.solvers.FirstAvailableCarriageSolver;
import com.github.onetraintransferproblemgenerator.solvers.GreedyPassengerOrderSolver;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.greedyall.GreedyAllPassengersSolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class OneTrainTransferProblemGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneTrainTransferProblemGeneratorApplication.class, args);

        /**
        OrchestrationParameters parameters = new OrchestrationParameters();
        parameters.setCsvFilePath("./metadata.csv");
        parameters.setInstanceCount(500);
        parameters.setInstanceIdPrefix("realistic_");
        parameters.setGenerator(new RealisticGenerator());

        List<Class<? extends OneTrainTransferSolver>> solvers = new ArrayList<>();
        solvers.add(FirstAvailableCarriageSolver.class);
        solvers.add(GreedyPassengerOrderSolver.class);
        solvers.add(GreedyAllPassengersSolver.class);
        parameters.setSolvers(solvers);

        SimpleOrchestration orchestration = new SimpleOrchestration(parameters);

        orchestration.runOrchestration();

        parameters.setCsvFilePath("./metadata_simple.csv");
        parameters.setInstanceCount(200);
        parameters.setInstanceIdPrefix("simple_");
        parameters.setGenerator(new SimpleGenerator());
        orchestration = new SimpleOrchestration(parameters);
        orchestration.runOrchestration();
         */
    }

}
