package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.Data;

@Data
class ExpandInstanceIndividual {

    private ProblemInstance problemInstance;
    private StationCapacityTracker stationCapacityTracker;

    public ExpandInstanceIndividual(ProblemInstance problemInstance) {
        this.problemInstance = problemInstance;
        OneTrainTransferProblem problem = problemInstance.getProblem();
        stationCapacityTracker = new StationCapacityTracker(problem);
    }

    public ExpandInstanceIndividual deepClone() {
        ProblemInstance copy = problemInstance.deepClone();
        return new ExpandInstanceIndividual(copy);
    }

    public void addPassenger(Passenger p) {
        problemInstance.getProblem().getPassengers().add(p);
        stationCapacityTracker.addPassenger(p);
    }

}
