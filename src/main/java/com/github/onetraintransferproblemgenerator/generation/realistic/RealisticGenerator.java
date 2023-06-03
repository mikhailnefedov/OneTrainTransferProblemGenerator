package com.github.onetraintransferproblemgenerator.generation.realistic;

import com.github.onetraintransferproblemgenerator.generation.OneTrainTransferProblemGenerator;
import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;

import java.util.List;

public class RealisticGenerator implements OneTrainTransferProblemGenerator {

    public RealisticGenerator() {

    }

    @Override
    public OneTrainTransferProblem generate() {
        return null;
    }

    public static void main(String[] args) {
        List<Station> result = RealisticDataReader.getStations();
        System.out.println(result.toString());
        List<Train> trains = RealisticDataReader.getTrains();
        System.out.println(trains.toString());
    }
}
