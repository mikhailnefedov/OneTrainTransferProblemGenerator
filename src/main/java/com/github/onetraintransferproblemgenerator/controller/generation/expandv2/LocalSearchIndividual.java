package com.github.onetraintransferproblemgenerator.controller.generation.expandv2;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.StationCapacityTracker;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import lombok.Data;
import org.ejml.simple.SimpleMatrix;

@Data
public class LocalSearchIndividual {

    private ProblemInstance problemInstance;
    private StationCapacityTracker stationCapacityTracker;
    private SimpleMatrix coordinates;
    /**
     * In this case fitness: distance to a target point
     */
    private double fitness;

    public LocalSearchIndividual(ProblemInstance problemInstance, SimpleMatrix coordinates, double fitness) {
        this.problemInstance = problemInstance;
        stationCapacityTracker = new StationCapacityTracker(problemInstance.getProblem());
        this.coordinates = coordinates;
        this.fitness = fitness;
    }


    public LocalSearchIndividual deepClone() {
        ProblemInstance copy = problemInstance.deepClone();
        return new LocalSearchIndividual(copy, coordinates, fitness);
    }


    public void addPassenger(Passenger p) {
        problemInstance.getProblem().getPassengers().add(p);
        stationCapacityTracker.addPassenger(p);
    }
}
