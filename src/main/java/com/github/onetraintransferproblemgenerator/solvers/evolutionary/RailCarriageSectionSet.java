package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.models.Passenger;

import java.util.HashSet;
import java.util.Set;

public class RailCarriageSectionSet {

    private int capacity;
    private Set<Passenger> passengers;

    public RailCarriageSectionSet(int capacity) {
        this.capacity = capacity;
        passengers = new HashSet<>();
    }

    public void addPassenger(Passenger passenger) {
        if (capacity > 0)
            passengers.add(passenger);
            capacity -= 1;
    }

    public boolean hasFreeCapacity() {
        return capacity > 0;
    }

    public void removePassenger(Passenger passenger) {
        passengers.remove(passenger);
        capacity += 1;
    }

}
