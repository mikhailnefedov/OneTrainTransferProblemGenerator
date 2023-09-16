package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.generation.BaseGenerator;
import com.github.onetraintransferproblemgenerator.generation.simple.SimpleGenerator;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.StationTuple;
import com.github.onetraintransferproblemgenerator.persistence.ConflictEvolutionDataRepository;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("conflict")
public class ConflictEvolutionController {

    private final String INSTANCE_ID_PREFIX = "conflict_evo_";
    private final int POPULATION_SIZE = 20;
    private final double MUTATION_RATE = 0.5;
    private final int GENERATION_COUNT = 200;
    private final ConflictEvolutionDataRepository conflictEvolutionDataRepository;
    private List<ConflictCoordinate> targetPoints;
    private ConflictEvolutionMutation mutation;

    public ConflictEvolutionController(ConflictEvolutionDataRepository conflictEvolutionDataRepository) {
        this.conflictEvolutionDataRepository = conflictEvolutionDataRepository;
        targetPoints = new ArrayList<>(List.of(
            new ConflictCoordinate(0.0, 1.0),
            new ConflictCoordinate(0.5, 1.0),
            new ConflictCoordinate(1.0, 1.0),
            new ConflictCoordinate(1.0, 0.5),
            new ConflictCoordinate(1.0, 0.0),
            new ConflictCoordinate(0.5, 0.0),
            new ConflictCoordinate(0.0, 0.0),
            new ConflictCoordinate(0.0, 0.5)
        ));
    }


    @PostMapping("evolution")
    void init(@RequestBody ConflictEvolutionParameters parameters) {

        configureMutation(parameters.getMutationName());

        List<ConflictEvolutionData> data =
            IntStream.range(1, parameters.getInstanceCount() + 1).boxed().toList()
                .parallelStream()
                .map(i -> {
                    String instanceId = INSTANCE_ID_PREFIX + i;
                    return applyConflictEvolution(parameters.getExperimentId(), instanceId);
                })
                .filter(Objects::nonNull)
                .toList();

        conflictEvolutionDataRepository.saveAll(data);
    }

    private void configureMutation(String mutationName) {
        if (mutationName.equals("MoveInOrOutPositionMutation")) {
            mutation = new MoveInOrOutPositionMutation();
        } else {
            mutation = new ChangeOptimalRailCarriageMutation();
        }
    }

    private BaseGenerator configureGenerator() {
        BaseGenerator generator = new SimpleGenerator();
        generator.setMIN_CONGESTION(0.8);
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

        individual.computePossibleToCreateConflicts();

        return individual;
    }

    private void computeMaxPositionOfStations(ConflictEvolutionIndividual individual, OneTrainTransferProblem problem) {
        int trainSize = problem.getTrain().getRailCarriages().size();
        List<StationTuple> stations = problem.getTrain().getStations();

        int threshold = 2;

        Map<Integer, Integer> maxPositionOfStation = stations.stream()
            .collect(Collectors.toMap(StationTuple::getStationId, station -> station.getStationOperation().getPosition() + trainSize + threshold));

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

    private ConflictEvolutionData applyConflictEvolution(String experimentId, String instanceId) {
        Random random = new Random();

        ConflictEvolutionData data = new ConflictEvolutionData(experimentId, instanceId);

        BaseGenerator generator = configureGenerator();

        ConflictEvolutionIndividual startIndividual = createStartIndividual(generator, experimentId);
        data.setStartCoordinates(ConflictCoordinate.convertFromIndividual(startIndividual));
        List<ConflictEvolutionIndividual> startPopulation = createStartPopulation(startIndividual);
        data.setStartGeneration(startPopulation.stream().map(ConflictCoordinate::convertFromIndividual).toList());

        if (!startIndividual.isPossibleToCreateConflicts())
            return null;

        System.out.println("Conflict Evolution of isntance: " + instanceId);

        for (ConflictCoordinate targetPoint : targetPoints) {
            List<ConflictEvolutionIndividual> population = startPopulation;
            double currentBestFitness = getBestIndividual(population).getFitness();
            data.initNewCoordinateHistory();

            for (int i = 0; i < GENERATION_COUNT; i++) {
                List<ConflictEvolutionIndividual> newPopulation = new ArrayList<>();
                List<ConflictEvolutionIndividual> parents = TournamentSelection.select(population, POPULATION_SIZE * 2);
                for (int j = 0; j < POPULATION_SIZE; j++) {
                    ConflictEvolutionIndividual child = doCrossover(parents.get(j), parents.get(j + 1));
                    if (random.nextDouble() < MUTATION_RATE) {
                        mutation.mutate(child);
                    }

                    InstanceFeatureDescription description1 = FeatureExtractor.extract(instanceId, child.getProblemInstance().getProblem());
                    child.getProblemInstance().setFeatureDescription(description1);
                    child.computeAndSetFitness(targetPoint);
                    newPopulation.add(child);
                }
                population = newPopulation;
                ConflictEvolutionIndividual bestIndividual = getBestIndividual(population);
                if (bestIndividual.getFitness() < currentBestFitness) {
                    data.addCoordinate(bestIndividual);
                    currentBestFitness = bestIndividual.getFitness();
                }
            }
        }

        return data;
    }

    private ConflictEvolutionIndividual doCrossover(ConflictEvolutionIndividual parent1, ConflictEvolutionIndividual parent2) {
        return SplitCrossover.doCrossover(parent1, parent2);
    }

    private ConflictEvolutionIndividual getBestIndividual(List<ConflictEvolutionIndividual> population) {
        return population.stream().max(Comparator.comparingDouble(ConflictEvolutionIndividual::getFitness)).get();
    }
}
