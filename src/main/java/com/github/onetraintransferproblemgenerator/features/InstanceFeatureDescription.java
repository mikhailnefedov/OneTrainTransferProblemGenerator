package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.serialization.CsvName;
import com.github.onetraintransferproblemgenerator.solvers.FirstAvailableCarriageSolver;
import com.github.onetraintransferproblemgenerator.solvers.GreedyPassengerOrderSolver;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.greedyall.GreedyAllPassengersSolver;
import lombok.Data;

@Data
public class InstanceFeatureDescription {
    @CsvName(column = "Instances")
    private String instanceId;
    @CsvName(column = "Source")
    private String source;
    @CsvName(column = "feature_stationCount")
    private int stationCount;
    @CsvName(column = "feature_directionChangeCount")
    private int directionChangeCount;
    @CsvName(column = "feature_totalPassengerCount")
    private int totalPassengerCount;
    @CsvName(column = "feature_avgPassengerCount")
    private double averagePassengerCount;
    @CsvName(column = "feature_avgPassengerRouteLength")
    private double averagePassengerRouteLength;
    @CsvName(column = "feature_avgRailCarriageCapacity")
    private double averageRailCarriageCapacity;
    @CsvName(column = "feature_stdDevRailCarriageCapacity")
    private double standardDeviationRailCarriageCapacity;
    @CsvName(column = "feature_avgCongestion")
    private double averageCongestion;
    @CsvName(column = "algo_firstAvailableCarriage")
    private double firstAvailableCarriageCost = Double.NaN;
    @CsvName(column = "algo_greedyPassengerOrder")
    private double greedyPassengerOrderCost = Double.NaN;
    @CsvName(column = "algo_greedyAllPassenger")
    private double greedyAllPassengerCost = Double.NaN;

    public void setAlgorithmCost(double cost, Class<? extends OneTrainTransferSolver> solverClass) {
        if (solverClass.equals(FirstAvailableCarriageSolver.class)) {
            setFirstAvailableCarriageCost(cost);
        } else if (solverClass.equals(GreedyPassengerOrderSolver.class)) {
            setGreedyPassengerOrderCost(cost);
        } else if (solverClass.equals(GreedyAllPassengersSolver.class)) {
            setGreedyAllPassengerCost(cost);
        }
    }
}
