package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.serialization.CsvName;
import com.github.onetraintransferproblemgenerator.solvers.*;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.KnownSolutionsFCAPSMEvolutionarySolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.KnownSolutionsFCAPSMNITEvolutionarySolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.KnownSolutionsFCMEvolutionarySolver;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.RandomSolutionsFCMEvolutionarySolver;
import com.github.onetraintransferproblemgenerator.solvers.greedyall.GreedyAllPassengersSolver;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @CsvName(column = "algo_fillRailCarriages")
    private double fillRailCarriagesCost = Double.NaN;
    @CsvName(column = "algo_greedyPassengerOrder")
    private double greedyPassengerOrderCost = Double.NaN;
    @CsvName(column = "algo_greedyAllPassenger")
    private double greedyAllPassengerCost = Double.NaN;
    @CsvName(column = "algo_shortestRidesFirstCost")
    private double shortestRidesFirstCost = Double.NaN;
    @CsvName(column = "algo_longestRidesFirstCost")
    private double longestRidesFirstCost = Double.NaN;
    @CsvName(column = "algo_greedyPassengerReverseOrder")
    private double greedyPassengerReverseOrderCost = Double.NaN;
    @CsvName(column = "algo_randomSolutionsFCMEvolutionaryCost")
    private double randomSolutionsFCMEvolutionaryCost = Double.NaN;
    @CsvName(column = "algo_knownSolutionsFCMEvolutionaryCost")
    private double knownSolutionsFCMEvolutionaryCost = Double.NaN;
    @CsvName(column = "algo_knownSolutionsFCAPSMEvolutionaryCost")
    private double knownSolutionsFCAPSMEvolutionaryCost = Double.NaN;
    @CsvName(column = "algo_knownSolutionsFCAPSMNITEvolutionaryCost")
    private double knownSolutionsFCAPSMNITEvolutionaryCost = Double.NaN;

    public void setAlgorithmCost(double cost, Class<? extends OneTrainTransferSolver> solverClass) {
        if (solverClass.equals(FillRailCarriagesSolver.class)) {
            setFillRailCarriagesCost(cost);
        } else if (solverClass.equals(GreedyPassengerOrderSolver.class)) {
            setGreedyPassengerOrderCost(cost);
        } else if (solverClass.equals(GreedyAllPassengersSolver.class)) {
            setGreedyAllPassengerCost(cost);
        } else if (solverClass.equals(ShortestRidesFirstSolver.class)) {
            setShortestRidesFirstCost(cost);
        } else if (solverClass.equals(KnownSolutionsFCMEvolutionarySolver.class)) {
            setKnownSolutionsFCMEvolutionaryCost(cost);
        } else if (solverClass.equals(LongestRidesFirstSolver.class)) {
            setLongestRidesFirstCost(cost);
        } else if (solverClass.equals(GreedyPassengerReverseOrderSolver.class)) {
            setGreedyPassengerReverseOrderCost(cost);
        } else if (solverClass.equals(RandomSolutionsFCMEvolutionarySolver.class)) {
            setRandomSolutionsFCMEvolutionaryCost(cost);
        } else if (solverClass.equals(KnownSolutionsFCAPSMEvolutionarySolver.class)) {
            setKnownSolutionsFCAPSMEvolutionaryCost(cost);
        } else if (solverClass.equals(KnownSolutionsFCAPSMNITEvolutionarySolver.class)) {
            setKnownSolutionsFCAPSMNITEvolutionaryCost(cost);
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
                .collect(Collectors.toList());
    }

    public List<Tuple<String, Double>> getAlgorithmCosts() {
        List<Field> declaredFields = List.of(InstanceFeatureDescription.class.getDeclaredFields());
        return declaredFields.stream()
            .filter(f -> f.getName().contains("Cost"))
            .peek(f -> f.setAccessible(true))
            .map(f -> {
                try {
                    return new Tuple<>(f.getName(), f.getDouble(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            })
            .toList();
    }

    public static List<String> getAlgorithmNames() {
        List<Field> declaredFields = List.of(InstanceFeatureDescription.class.getDeclaredFields());
        return declaredFields.stream()
            .filter(f -> f.getName().contains("Cost"))
            .map(Field::getName)
            .toList();
    }
}
