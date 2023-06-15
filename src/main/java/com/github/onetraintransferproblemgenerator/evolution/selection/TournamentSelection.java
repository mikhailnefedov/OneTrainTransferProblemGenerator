package com.github.onetraintransferproblemgenerator.evolution.selection;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TournamentSelection {

    private final int TOURNAMENTS_COUNT = 3;

    //TODO Finish?
    public List<OneTrainTransferProblem> select(List<Tuple<ProblemInstance, List<Double>>> individuals, int parentsCount) {
        Random random = new Random();

        for (int i = 0; i < parentsCount; i++) {
            Tuple<ProblemInstance, List<Double>> currentWinner = individuals.get(random.nextInt(individuals.size()));
            for (int j = 0; j < TOURNAMENTS_COUNT; j++) {
                Tuple<ProblemInstance, List<Double>> challenger = individuals.get(random.nextInt(individuals.size()));

            }
        }
        return new ArrayList<>();
    }
}
