package com.github.onetraintransferproblemgenerator.solvers.evolutionary;

import com.github.onetraintransferproblemgenerator.exceptions.NotEnoughTrainCapacityException;
import lombok.SneakyThrows;

public class RailCarriageSectionSet {

    private int capacity;

    public RailCarriageSectionSet(int capacity) {
        this.capacity = capacity;
    }

    @SneakyThrows
    public void addPassenger() {
        if (capacity > 0) {
            capacity -= 1;
        } else {
            throw new NotEnoughTrainCapacityException(capacity);
        }

    }

    public boolean hasFreeCapacity() {
        return capacity > 0;
    }

    public void removePassenger() {
        capacity += 1;
    }

}
