package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.serialization.CsvName;
import com.github.onetraintransferproblemgenerator.solvers.FirstAvailableCarriageSolver;
import com.github.onetraintransferproblemgenerator.solvers.GreedyPassengerOrderSolver;
import com.github.onetraintransferproblemgenerator.solvers.OneTrainTransferSolver;
import com.github.onetraintransferproblemgenerator.solvers.ShortestRidesFirstSolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.EvolutionarySolver;
import com.github.onetraintransferproblemgenerator.solvers.greedyall.GreedyAllPassengersSolver;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

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
    private double avgPassengerCount;
    @CsvName(column = "feature_avgPassengerRouteLength")
    private double avgPassengerRouteLength;
    @CsvName(column = "feature_avgRailCarriageCapacity")
    private double avgRailCarriageCapacity;
    @CsvName(column = "feature_stdDevRailCarriageCapacity")
    private double stdDevRailCarriageCapacity;
    @CsvName(column = "feature_avgCongestion")
    private double avgCongestion;
    @CsvName(column = "feature_decisionPoints")
    private double decisionPoints;
    @CsvName(column = "algo_firstAvailableCarriage")
    private double firstAvailableCarriageCost = Double.NaN;
    @CsvName(column = "algo_greedyPassengerOrder")
    private double greedyPassengerOrderCost = Double.NaN;
    @CsvName(column = "algo_greedyAllPassenger")
    private double greedyAllPassengerCost = Double.NaN;
    @CsvName(column = "algo_shortestRidesFirstCost")
    private double shortestRidesFirstCost = Double.NaN;
    @CsvName(column = "algo_evolutionaryCost")
    private double evolutionaryCost = Double.NaN;

    public void setAlgorithmCost(double cost, Class<? extends OneTrainTransferSolver> solverClass) {
        if (solverClass.equals(FirstAvailableCarriageSolver.class)) {
            setFirstAvailableCarriageCost(cost);
        } else if (solverClass.equals(GreedyPassengerOrderSolver.class)) {
            setGreedyPassengerOrderCost(cost);
        } else if (solverClass.equals(GreedyAllPassengersSolver.class)) {
            setGreedyAllPassengerCost(cost);
        } else if (solverClass.equals(ShortestRidesFirstSolver.class)) {
            setShortestRidesFirstCost(cost);
        } else if (solverClass.equals(EvolutionarySolver.class)) {
            setEvolutionaryCost(cost);
        }
    }

    public List<Tuple<String, Double>> getFeatureVector(List<String> featureNames) {
        List<Field> declaredFields = List.of(InstanceFeatureDescription.class.getDeclaredFields());
        return declaredFields.stream()
                .peek(f -> f.setAccessible(true))
                .map(f -> {
                    try {
                        return new Tuple<>(f.getName(), Double.parseDouble(f.get(this).toString()));
                    } catch (IllegalAccessException | NumberFormatException ignored) {
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .filter(t -> featureNames.contains(t.getLeft()))
                .toList();
    }
}
