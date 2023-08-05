package com.github.onetraintransferproblemgenerator.controller.generation.expandv2.mutations;

import com.github.onetraintransferproblemgenerator.controller.generation.expandv2.LocalSearchIndividual;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;

import java.util.List;
import java.util.Random;

public class RemoveCapacityMutation implements LocalSearchMutation {

    @Override
    public LocalSearchIndividual mutate(LocalSearchIndividual individual) {
        if (oneSeatIsRemovable(individual)) {
            Random random = new Random();
            List<RailCarriage> railCarriages = individual.getProblemInstance().getProblem().getTrain().getRailCarriages();
            RailCarriage carriageToModify = railCarriages.get(random.nextInt(railCarriages.size()));
            int newCapacity = carriageToModify.getCapacity() - 1;

            carriageToModify.setCapacity(newCapacity);
            individual.getStationCapacityTracker().removeOneSeat();
        }
        return individual;
    }

    private boolean oneSeatIsRemovable(LocalSearchIndividual individual) {
        List<Integer> stationIds = individual.getProblemInstance().getProblem().getTrain().getStationIds();
        int startStation = stationIds.get(0);
        int endStation = stationIds.get(stationIds.size() - 1);
        List<Tuple<Integer, Integer>> availableRides = individual.getStationCapacityTracker().getAvailableRides();
        return availableRides.stream().anyMatch(tuple -> tuple.getLeft() == startStation && tuple.getRight() == endStation);
    }
}
