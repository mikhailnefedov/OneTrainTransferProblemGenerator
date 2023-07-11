package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

import java.util.List;
import java.util.Random;

public class FreeCapacitySwapMutation {

    private Random random;

    public void mutate(Individual individual) {
        random = new Random();
        if (individual.getPassengerRailCarriageMapping().keySet().size() > 0) {
            Passenger passenger = chooseRandomPassenger(individual);

            List<? extends Swap> swaps = SwapCreator.createSwapsWithFreeCapacity(individual, passenger);
            if (swaps.size() > 0) {
                Swap swap = swaps.get(random.nextInt(swaps.size()));
                swap.makeSwap(individual, passenger);
            }
        }
    }

    private Passenger chooseRandomPassenger(Individual individual) {
        List<Passenger> passengers = individual.getPassengerRailCarriageMapping().keySet().stream().toList();

        return passengers.get(random.nextInt(passengers.size()));
    }

}
