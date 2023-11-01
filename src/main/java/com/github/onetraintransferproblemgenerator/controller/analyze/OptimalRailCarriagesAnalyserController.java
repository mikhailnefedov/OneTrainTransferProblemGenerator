package com.github.onetraintransferproblemgenerator.controller.analyze;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstance;
import com.github.onetraintransferproblemgenerator.persistence.ProblemInstanceRepository;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriageDistance;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Get passenger count for every rail carriage (optimal rail carriage)
 */
@RestController
@RequestMapping("optimalrailcarriages")
public class OptimalRailCarriagesAnalyserController {

    private final ProblemInstanceRepository problemInstanceRepository;

    public OptimalRailCarriagesAnalyserController(ProblemInstanceRepository problemInstanceRepository) {
        this.problemInstanceRepository = problemInstanceRepository;
    }

    @PostMapping("analyse")
    public ResponseEntity analyse(@RequestBody AnalyzerData analyzerData) {
        ProblemInstance instance = problemInstanceRepository.findByExperimentIdAndInstanceId(analyzerData.getExperimentId(), analyzerData.getInstanceId());

        OneTrainTransferProblem problem = instance.getProblem();

        HashMap<Integer, Integer> passengersOfOptimalRailCarriage = new HashMap<>();
        for (RailCarriage rc : problem.getTrain().getRailCarriages()) {
            passengersOfOptimalRailCarriage.put(rc.getSequenceNumber(), 0);
        }

        RailCarriagePositionHelper railCarriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());

        List<Integer> stationIds = problem.getTrain().getStationIds();

        for (int stationId : stationIds) {
            List<Passenger> inPassengers = problem.getInPassengersOfStation(stationId);
            for (Passenger p : inPassengers) {
                List<RailCarriageDistance> railCarriageDistances = railCarriagePositionHelper.getDistancesForRailCarriages(p);
                railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCost));
                int optimalId = railCarriageDistances.get(0).getRailCarriageId();
                int currentCount = passengersOfOptimalRailCarriage.get(optimalId);
                passengersOfOptimalRailCarriage.put(optimalId, currentCount + 1);
            }
        }

        return ResponseEntity.ok(passengersOfOptimalRailCarriage);
    }

}
