package com.github.onetraintransferproblemgenerator.controller.generation.expandv2.mutations;

import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;

public abstract class PassengerMutation implements LocalSearchMutation {

    protected int getNextPassengerId(ProblemInstance instance) {
        return instance.getProblem().getPassengers().get(instance.getProblem().getPassengers().size() - 1).getId() + 1;
    }
}
