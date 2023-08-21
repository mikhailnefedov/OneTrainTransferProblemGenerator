package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.generation.BaseGenerator;
import com.github.onetraintransferproblemgenerator.generation.simple.SimpleGenerator;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.StationTuple;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("expandfeatures")
public class ConflictEvolutionController {

    private final String INSTANCE_ID_PREFIX = "conflict_evo_";
    private final int POPULATION_SIZE = 20;
    private final double MUTATION_RATE = 0.5;

    @PostMapping("init")
    void init(@RequestBody String experimentId) {

        BaseGenerator generator = configureGenerator();

        ConflictEvolutionIndividual individual = createStartIndividual(generator, experimentId);

        List<ConflictEvolutionIndividual> population = createStartPopulation(individual);

        Random random = new Random();

        int generationCount = 200;
        for (int i = 0; i < generationCount; i++) {
            List<ConflictEvolutionIndividual> newPopulation = new ArrayList<>();
            List<ConflictEvolutionIndividual> parents = TournamentSelection.select(population, POPULATION_SIZE * 2);
            for (int j = 0; j < POPULATION_SIZE; j++) {
                ConflictEvolutionIndividual parent1 = parents.get(j);
                ConflictEvolutionIndividual parent2 = parents.get(j + 1);

                ConflictEvolutionIndividual child = SplitCrossover.doCrossover(parent1, parent2);
                if (random.nextDouble() < MUTATION_RATE) {
                    MoveInOrOutPositionMutation.doMutation(child);
                }

                InstanceFeatureDescription description1 = FeatureExtractor.extract(INSTANCE_ID_PREFIX, child.getProblemInstance().getProblem());
                child.getProblemInstance().setFeatureDescription(description1);
                child.computeAndSetFitness();
                newPopulation.add(child);
            }
            population = newPopulation;
            double bestFitness = population.stream().max(Comparator.comparingDouble(ConflictEvolutionIndividual::getFitness)).get().getFitness();
            System.out.println(bestFitness);
        }
        //population.toString();
    }

    private BaseGenerator configureGenerator() {
        BaseGenerator generator = new SimpleGenerator();
        generator.setMIN_CONGESTION(0.4);
        return generator;
    }

    private ConflictEvolutionIndividual createStartIndividual(BaseGenerator generator, String experimentId) {
        OneTrainTransferProblem problem = generator.generate();
        InstanceFeatureDescription description = FeatureExtractor.extract(INSTANCE_ID_PREFIX, problem);

        ProblemInstance instance = new ProblemInstance(problem, experimentId, this.getClass(), INSTANCE_ID_PREFIX);
        instance.setFeatureDescription(description);

        ConflictEvolutionIndividual individual = new ConflictEvolutionIndividual();
        computeMaxPositionOfStations(individual, problem);
        individual.setProblemInstance(instance);

        double blockedPassengerRatio = individual.getProblemInstance().getFeatureDescription().getBlockedPassengerRatio();
        double conflictFreePassengerSeatingRatio = individual.getProblemInstance().getFeatureDescription().getConflictFreePassengerSeatingRatio();
        individual.setOriginalCoordinates(new ArrayList<>(List.of(blockedPassengerRatio, conflictFreePassengerSeatingRatio)));
        individual.computeAndSetFitness();

        return individual;
    }

    private void computeMaxPositionOfStations(ConflictEvolutionIndividual individual, OneTrainTransferProblem problem) {
        int trainSize = problem.getTrain().getRailCarriages().size();
        List<StationTuple> stations = problem.getTrain().getStations();

        Map<Integer, Integer> maxPositionOfStation = stations.stream()
            .collect(Collectors.toMap(StationTuple::getStationId, station -> station.getStationOperation().getPosition() + trainSize));

        individual.setMaxPositionOfStation(maxPositionOfStation);
    }

    private List<ConflictEvolutionIndividual> createStartPopulation(ConflictEvolutionIndividual originalIndividual) {
        List<ConflictEvolutionIndividual> startPopulation = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            ConflictEvolutionIndividual copyInd = originalIndividual.deepClone();
            copyInd.randomizeInAndOutPositions();

            InstanceFeatureDescription description = FeatureExtractor.extract(INSTANCE_ID_PREFIX, copyInd.getProblemInstance().getProblem());
            copyInd.getProblemInstance().setFeatureDescription(description);

            startPopulation.add(copyInd);
        }
        return startPopulation;
    }
}
