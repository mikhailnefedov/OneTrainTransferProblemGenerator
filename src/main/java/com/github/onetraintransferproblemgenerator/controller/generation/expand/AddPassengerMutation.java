package com.github.onetraintransferproblemgenerator.controller.generation.expand;

import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;

class AddPassengerMutation implements Mutation {

    @Override
    public ExpandInstanceIndividual mutate(ExpandInstanceIndividual individual) {
        ProblemInstance problemInstanceCopy = new ProblemInstance();

        //TODO: Build mutation with own object, object should contain map for each station and capacity

        return individual;
    }
}
