package com.github.onetraintransferproblemgenerator.solvers.evolutionary.operators.mutation;

import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.Individual;

public interface Mutation {
    void mutate(Individual individual);
}
