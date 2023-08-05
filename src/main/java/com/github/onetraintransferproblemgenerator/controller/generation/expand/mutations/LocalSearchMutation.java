package com.github.onetraintransferproblemgenerator.controller.generation.expand.mutations;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.LocalSearchIndividual;

public interface LocalSearchMutation {
    LocalSearchIndividual mutate(LocalSearchIndividual individual);
}
