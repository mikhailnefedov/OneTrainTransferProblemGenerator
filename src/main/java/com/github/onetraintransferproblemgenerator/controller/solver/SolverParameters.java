package com.github.onetraintransferproblemgenerator.controller.solver;

import com.github.onetraintransferproblemgenerator.solvers.evolutionary.SolverConfiguration;
import lombok.Data;

class SolverParameters {

}

@Data
class DeterministicSolverParameters {
    private String experimentId;
    private String solverClass;
}

@Data
class EvolutionarySolverParameters {
    private String experimentId;
    private String solverClass;
    private SolverConfiguration solverConfiguration;
}
