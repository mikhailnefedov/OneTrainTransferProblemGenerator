package com.github.onetraintransferproblemgenerator.solvers.evolutionary.models;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class SolutionAndHistoryData {
    private HashMap<Passenger, Integer> passengerMapping;
    private HistoricalEvolutionData historicalData;
}
