package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.features.FeatureExtractor;
import com.github.onetraintransferproblemgenerator.features.InstanceFeatureDescription;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.StationTuple;
import com.github.onetraintransferproblemgenerator.persistence.ConflictEvolutionDataRepository;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

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
    }

    @PostMapping(value = "evolution", consumes = "application/json")
    void init(@RequestBody ConflictEvolutionParameters parameters) {

        configureMutation(parameters.getMutationName());
        targetPoints = parameters.getConflictCoordinates();

        ConflictEvolutionData data = applyConflictEvolution(parameters.getExperimentId(), parameters.getInstance().convertToProblemInstance());

        conflictEvolutionDataRepository.save(data);
    }

    private void configureMutation(String mutationName) {
        if (mutationName.equals("MoveInOrOutPositionMutation")) {
            mutation = new MoveInOrOutPositionMutation();
        } else {
            mutation = new ChangeOptimalRailCarriageMutation();
        }
    }

    /**
     * private BaseGenerator configureGenerator() {
     * BaseGenerator generator = new SimpleGenerator();
     * generator.setMIN_CONGESTION(0.8);
     * return generator;
     * }
     */

    private ConflictEvolutionIndividual createStartIndividual(ProblemInstance instance, String experimentId) {
        instance.setExperimentId(experimentId);
        OneTrainTransferProblem problem = instance.getProblem();
        //InstanceFeatureDescription description = instance.getFeatureDescription();

        //ProblemInstance instance = new ProblemInstance(problem, experimentId, this.getClass(), INSTANCE_ID_PREFIX);
        //instance.setFeatureDescription(description);

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

    private ConflictEvolutionData applyConflictEvolution(String experimentId, ProblemInstance instance) {
        Random random = new Random();
        ConflictEvolutionData data = new ConflictEvolutionData(experimentId, instance.getInstanceId());

        ConflictEvolutionIndividual startIndividual = createStartIndividual(instance, experimentId);
        data.setStartCoordinates(ConflictCoordinate.convertFromIndividual(startIndividual));
        List<ConflictEvolutionIndividual> startPopulation = createStartPopulation(startIndividual);
        data.setStartGeneration(startPopulation.stream().map(ConflictCoordinate::convertFromIndividual).toList());

        if (!startIndividual.isPossibleToCreateConflicts())
            return null;

        String instanceId = instance.getInstanceId();
        System.out.println("Conflict Evolution of instance: " + instanceId);
        for (ConflictCoordinate targetPoint : targetPoints) {
            System.out.println("Conflict Evolution for target point: " + targetPoint);
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
                    System.out.println("Found better individual");
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
