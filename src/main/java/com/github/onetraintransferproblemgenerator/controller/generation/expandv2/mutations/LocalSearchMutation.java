package com.github.onetraintransferproblemgenerator.controller.generation.expandv2.mutations;

import com.github.onetraintransferproblemgenerator.controller.generation.expandv2.LocalSearchIndividual;

public interface LocalSearchMutation {
    LocalSearchIndividual mutate(LocalSearchIndividual individual);
}
