package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

public class RailCarriageSectionSet {

    private int capacity;

    public RailCarriageSectionSet(int capacity) {
        this.capacity = capacity;
    }

    public void addPassenger() {
        if (capacity > 0)
            capacity -= 1;
    }

    public boolean hasFreeCapacity() {
        return capacity > 0;
    }

    public void removePassenger() {
        capacity += 1;
    }

}
