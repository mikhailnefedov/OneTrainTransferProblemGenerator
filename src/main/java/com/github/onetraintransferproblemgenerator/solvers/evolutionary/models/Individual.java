package com.github.onetraintransferproblemgenerator.solvers.evolutionary.models;

import com.github.onetraintransferproblemgenerator.models.OneTrainTransferProblem;
import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;
import com.github.onetraintransferproblemgenerator.solvers.evolutionary.RailCarriageSectionSet;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class Individual {

    private HashMap<Integer, HashMap<Integer, RailCarriageSectionSet>> passengersOfRailCarriageOfInStation;
    private double fitness;
    private HashMap<Passenger, Integer> passengerRailCarriageMapping;

    public Individual(OneTrainTransferProblem problem) {
        initializePassengerMap(problem);
        passengerRailCarriageMapping = new HashMap<>();
    }

    public Individual(HashMap<Passenger, Integer> passengerRailCarriageMapping, OneTrainTransferProblem problem) {
        initializePassengerMap(problem);
        for (Passenger passenger : passengerRailCarriageMapping.keySet()) {
            int railCarriageId = passengerRailCarriageMapping.get(passenger);
            for (int stationId = passenger.getInStation(); stationId < passenger.getOutStation(); stationId++) {
                passengersOfRailCarriageOfInStation.get(stationId).get(railCarriageId).addPassenger(passenger);
            }
        }
        this.passengerRailCarriageMapping = passengerRailCarriageMapping;
    }

    private void initializePassengerMap(OneTrainTransferProblem problem) {
        passengersOfRailCarriageOfInStation = new HashMap<>();
        for (int i = 1; i < problem.getTrain().getStationIds().size(); i++) {
            passengersOfRailCarriageOfInStation.put(i, new HashMap<>());
            for (RailCarriage railCarriage : problem.getTrain().getRailCarriages()) {
                passengersOfRailCarriageOfInStation.get(i)
                        .put(railCarriage.getSequenceNumber(), new RailCarriageSectionSet(railCarriage.getCapacity()));
            }
        }
    }

    public int getRailCarriageIdOfPassenger(Passenger passenger) {
        try {
            return passengerRailCarriageMapping.get(passenger);
        } catch (Exception e) {
            int a = 2+2;
            System.out.println(e);
        }
        return 0;
    }

    public void addPassengerWithRailCarriageId(Passenger passenger, int railCarriageId) {
        passengerRailCarriageMapping.put(passenger, railCarriageId);
        for (int stationId = passenger.getInStation(); stationId < passenger.getOutStation(); stationId++) {
            passengersOfRailCarriageOfInStation.get(stationId).get(railCarriageId).addPassenger(passenger);
        }
    }

    public void addPassengerWithoutRailCarriage(Passenger passenger, List<Integer> railCarriagePreference) {
        for (Integer railCarriageId : railCarriagePreference) {
            boolean isCapacityAvailable =
                    capacityForRideAvailable(railCarriageId, passenger.getInStation(), passenger.getOutStation());
            if (isCapacityAvailable) {
                addPassengerWithRailCarriageId(passenger, railCarriageId);
                break;
            }
        }
    }

    private boolean capacityForRideAvailable(int railCarriageId, int inStation, int outStation) {
        for (int stationId = inStation; stationId < outStation; stationId++) {
            if (! passengersOfRailCarriageOfInStation.get(stationId).get(railCarriageId).hasFreeCapacity()) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> railCarriagesWithCapacityForRide(int inStation, int outStation) {
        List<Integer> railCarriageIds = new ArrayList<>();
        for (Integer railCarriageId : passengersOfRailCarriageOfInStation.get(1).keySet()) {
            if (capacityForRideAvailable(railCarriageId, inStation, outStation))
                railCarriageIds.add(railCarriageId);
        }
        return railCarriageIds;
    }
}
