package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TournamentSelection {

    private static final int TOURNAMENTS_COUNT = 3;

    public static List<ConflictEvolutionIndividual> select(List<ConflictEvolutionIndividual> individuals, int parentsCount) {
        Random random = new Random();
        List<ConflictEvolutionIndividual> parents = new ArrayList<>();

        for (int i = 0; i < parentsCount; i++) {
            ConflictEvolutionIndividual currentWinner = individuals.get(random.nextInt(individuals.size()));
            for (int j = 0; j < TOURNAMENTS_COUNT; j++) {
                ConflictEvolutionIndividual challenger = individuals.get(random.nextInt(individuals.size()));
                if (challenger.getFitness() < currentWinner.getFitness()) {
                    currentWinner = challenger;
                }
            }
            parents.add(currentWinner);
        }
        return parents;
    }
}
