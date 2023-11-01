## Instructions for usage

### Generating instances

Example post of generating some instances with the (minimal-controllable generator/minimal-kontrollierbarer Generator),
solving them thorugh the 4 algorithms and storing them in the database.

further configuration (congestion, max rail carriage capacity) is unfortunately
hard-coded in `com/github/onetraintransferproblemgenerator/generation/BaseGenerator.java`

SimpleGenerator: random trains, stations and passengers
RealisticGenerator: using predefined `data/trains.json` and `data/stations.json`
and no random position generation of passengers

```http
   POST http://localhost:8080/generation/generateinstances
   Content-Type: application/json
   {
    "experimentId" : "experiment",
    "storeInstances" : true,
    "generators": {
        "com.github.onetraintransferproblemgenerator.generation.simple.SimpleGenerator" : {
            "instanceCount": 100,
            "idPrefix": "mk_s_"
        },
        "com.github.onetraintransferproblemgenerator.generation.realistic.RealisticGenerator" : {
            "instanceCount": 100,
            "idPrefix": "mk_r_"
        }
    },
    "solvers": [
        "com.github.onetraintransferproblemgenerator.solvers.GreedyPassengerOrderSolver",
        "com.github.onetraintransferproblemgenerator.solvers.greedyall.GreedyAllPassengersSolver",
        "com.github.onetraintransferproblemgenerator.solvers.ShortestRidesFirstSolver",
        "com.github.onetraintransferproblemgenerator.solvers.LongestRidesFirstSolver"
    ]
}
   ```




