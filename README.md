# Instructions for docker

To start up use: `docker-compose up`

Unfortunately this part is more in an experimental stage and the whole functionality cannot be guaranteed.
The spring-boot app doesn't work reliably in a container while the other components seem okay. It is recommended to
startup the containers and terminate the spring-boot app and run it locally with `mvn spring-boot:run`


If there is already a dataset, you can create a database "test" in mongodb and a "probleminstances" collection and 
import the json data (Can be done with MongoDB Compass). If there is no database, just create some instances with a POST

# Instructions for usage

! : marking important features

## ! Generating instances

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

## Using an algorithm on already existing dataset
### ! Deterministic

To use a new algorithm on an already existing dataset use:

```http
   POST http://localhost:8080/deterministic/solve
   Content-Type: application/json
   {
    "experimentId" : "id",
    "solverClass" : "com.github.onetraintransferproblemgenerator.solvers.GreedyPassengerOrderSolver"
    }
   ```

### Evolutionary

```http
   POST http://localhost:8080/evolutionary/solve
   Content-Type: application/json
   {
    "experimentId" : "id",
    "solverClass" : "com.github.onetraintransferproblemgenerator.solvers.evolutionary.RandomSolutionsFCMEvolutionarySolver",
    "solverConfiguration" : {
        "populationSize" : 50,
        "parentsCount" : 20,
        "childrenCount" : 50,
        "generationCount" : 1000,
        "mutationRate" : 0.3,
        "generationsWithoutImprovement" : 50
    }
}
   ```

### ! Exporting csv

To export the data to a csv use (if you add new features or algorithms, ensure that they are computed and set in 
`InstanceFeatureDesciption.java`):

```http
   GET localhost:8080/export/csv/experimentId
   ```


## ! Local Search

First it is necessary to compute the prelim information of the dataset. Requirement: Using MATILDA to get
the selected features of SIFTED and projection matrix of PILOT. The request must contain those in order of the appearance in the 
exported CSV  (if not side-effects may happen).

post contains data for the actual PILOT 27-09-1000 dataset

```http
   POST http://localhost:8080/localsearch/init
   Content-Type: application/json
   {
    "experimentId": "27-09-1000",
    "transposedProjectionMatrix" : [
        [-0.3960,-0.3227],
        [0.1701,-0.6613],
        [0.4294,-0.7562],
        [-0.3783,-0.1508],
        [0.7470,0.3224]
    ],
    "featureNames" : ["totalPassengerCount", "passengerRatio","totalCongestion","blockedPassengerRatio", "conflictFreePassengerSeatingRatio"]
    }
   ```

After the initialization, it is possible to use local search in the instance space. Please look into [instance_space_expansion-27-09-1000.ipynb](https://github.com/mikhailnefedov/OneTrainTransferProblemGenerator/blob/main/python_backend/instance_space_expansion-27-09-1000.ipynb)
for usage and the visualization

## ! Conflict Evolution of passenger blockage features

Please look into [conflict_evolution-27-09-1000.ipynb](https://github.com/mikhailnefedov/OneTrainTransferProblemGenerator/blob/main/python_backend/conflict_evolution-27-09-1000.ipynb) for the usage and the visualization