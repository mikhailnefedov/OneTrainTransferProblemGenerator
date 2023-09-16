package com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.Train;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriageDistance;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;

import java.util.List;
import java.util.Random;

public class ChangeOptimalRailCarriageMutation implements ConflictEvolutionMutation {

    @Override
    public void mutate(ConflictEvolutionIndividual individual) {
        Random random = new Random();
        List<Passenger> passengers = individual.getProblemInstance().getProblem().getPassengers();
        Passenger passengerToChange = passengers.get(random.nextInt(passengers.size()));

        Train train = individual.getProblemInstance().getProblem().getTrain();
        RailCarriagePositionHelper carriagePositionHelper = new RailCarriagePositionHelper(train);
        List<RailCarriageDistance> railCarriageDistances = carriagePositionHelper.getDistancesForRailCarriages(passengerToChange);
        int optimalRailCarriageId = railCarriageDistances.get(0).getRailCarriageId();

        double randomDouble = random.nextDouble();
        if (randomDouble < 0.5 || optimalRailCarriageId == 1) {
            //move right
            movePositionsOfPassenger(passengerToChange, optimalRailCarriageId + 1, train);
        } else {
            //move left
            movePositionsOfPassenger(passengerToChange, optimalRailCarriageId - 1, train);
        }
    }

    private void movePositionsOfPassenger(Passenger p, int newOptimalCarriageId, Train train) {
        int newInPosition = train.getCarriagePosition(p.getInStation(), newOptimalCarriageId);
        int newOutPosition = train.getCarriagePosition(p.getOutStation(), newOptimalCarriageId);

        p.setInPosition(newInPosition);
        p.setOutPosition(newOutPosition);
    }
}
