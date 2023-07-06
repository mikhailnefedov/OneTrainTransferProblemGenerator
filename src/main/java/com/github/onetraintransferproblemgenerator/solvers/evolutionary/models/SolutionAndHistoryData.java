package com.github.onetraintransferproblemgenerator.solvers.evolutionary.models;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import lombok.Data;

import java.util.HashMap;

@Data
public class SolutionAndHistoryData {
    private HashMap<Passenger, Integer> passengerMapping;
    private HistoricalEvolutionData historicalData;
}
