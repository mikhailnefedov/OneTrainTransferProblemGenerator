package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.Data;
import org.ejml.simple.SimpleMatrix;

@Data
class ExpandInstanceIndividual {

    private ProblemInstance problemInstance;
    private StationCapacityTracker stationCapacityTracker;
    private SimpleMatrix coordinates;
    /**
     * In this case fitness: distance to a target point
     */
    private double fitness;

    public ExpandInstanceIndividual(ProblemInstance problemInstance, SimpleMatrix coordinates, double fitness) {
        this.problemInstance = problemInstance;
        stationCapacityTracker = new StationCapacityTracker(problemInstance.getProblem());

        this.coordinates = coordinates;
        this.fitness = fitness;
    }

    public ExpandInstanceIndividual deepClone() {
        ProblemInstance copy = problemInstance.deepClone();
        return new ExpandInstanceIndividual(copy, coordinates, fitness);
    }

    public void addPassenger(Passenger p) {
        problemInstance.getProblem().getPassengers().add(p);
        stationCapacityTracker.addPassenger(p);
    }

}
