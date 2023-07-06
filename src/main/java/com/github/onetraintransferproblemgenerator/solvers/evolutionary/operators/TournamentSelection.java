package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators;

import com.github.onetraintransferproblemgenerator.solvers.evolutionary.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TournamentSelection {

    private final static int TOURNAMENT_COUNT = 3; //q

    public static List<Individual> select(List<Individual> individuals, int selectionCount) {
        Random random = new Random();
        List<Individual> parents = new ArrayList<>();

        for (int i = 0; i < selectionCount; i++) {
            int index = random.nextInt(individuals.size());
            Individual currentWinner = individuals.get(index);
            for (int j = 0; j < TOURNAMENT_COUNT; j++) {
                int u = random.nextInt(individuals.size());
                Individual challenger = individuals.get(u);
                if (challenger.getFitness() < currentWinner.getFitness()) {
                    currentWinner = challenger;
                }
            }
            parents.add(currentWinner);
        }

        return parents;
    }
}
