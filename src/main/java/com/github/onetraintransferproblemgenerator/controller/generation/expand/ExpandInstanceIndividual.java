package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.solvers.SeatReservationStorage;
import lombok.Data;

@Data
class ExpandInstanceIndividual {

    private ProblemInstance problemInstance;
    private SeatReservationStorage capacityStorage;
    private Mutation mutation;

    public ExpandInstanceIndividual(ProblemInstance problemInstance, Mutation mutation) {
        this.problemInstance = problemInstance;
        this.mutation = mutation;
        OneTrainTransferProblem problem = problemInstance.getProblem();
        capacityStorage = new SeatReservationStorage(problem.getTrain());
        initializePassengers(problem);
    }

    private void initializePassengers(OneTrainTransferProblem problem) {
        for (Passenger p : problem.getPassengers()) {
            for (RailCarriage railCarriage : problem.getTrain().getRailCarriages()) {
                if (capacityStorage.isBoardingPossible(railCarriage.getSequenceNumber())) {
                    capacityStorage.inPassenger(railCarriage.getSequenceNumber(), p);
                    break;
                }
            }
        }
    }


}
