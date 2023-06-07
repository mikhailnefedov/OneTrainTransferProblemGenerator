package com.github.onetraintransferproblemgenerator.solvers.greedyall;

import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;
import com.github.onetraintransferproblemgenerator.models.Train;

import java.util.HashMap;

public class SectionCapacityRailCarriageStorage {

    private final HashMap<Integer, HashMap<Integer, Integer>> freeSectionCapacityOfRailCarriages;
    private final HashMap<Passenger, Integer> railCarriageOfPassenger;

    public SectionCapacityRailCarriageStorage(Train train) {
        freeSectionCapacityOfRailCarriages = new HashMap<>();
        railCarriageOfPassenger = new HashMap<>();

        for (RailCarriage railCarriage : train.getRailCarriages()) {
            HashMap<Integer, Integer> sectionCapacity =
                    freeSectionCapacityOfRailCarriages.put(railCarriage.getSequenceNumber(), new HashMap<>());
            for (int i = 0; i < train.getStationIds().size(); i++) {
                sectionCapacity.put(train.getStationIds().get(i), railCarriage.getCapacity());
            }
        }
    }

    public int inPassenger(int railCarriageId, Passenger passenger) {
        int inStation = passenger.getInStation();
        int outStation = passenger.getOutStation();

        HashMap<Integer, Integer> freeSectionCapacity = freeSectionCapacityOfRailCarriages.get(railCarriageId);

        for (int i = inStation; i < outStation; i++) {
            int newCapacityOfSection = freeSectionCapacity.get(i) - 1;
            freeSectionCapacity.put(i, newCapacityOfSection);
        }

        railCarriageOfPassenger.put(passenger, railCarriageId);

        return railCarriageId;
    }

    public boolean isBoardingPossible(int railCarriageId, Passenger passenger) {
        int inStation = passenger.getInStation();
        int outStation = passenger.getOutStation();

        HashMap<Integer, Integer> freeSectionCapacity = freeSectionCapacityOfRailCarriages.get(railCarriageId);

        for (int i = inStation; i < outStation; i++) {
            int capacityOfSection = freeSectionCapacity.get(i);
            if (capacityOfSection == 0) {
                return false;
            }
        }
        return true;
    }

    public int outPassenger(Passenger passenger) {
        int inStation = passenger.getInStation();
        int outStation = passenger.getOutStation();

        int railCarriageId = railCarriageOfPassenger.get(passenger);
        HashMap<Integer, Integer> freeSectionCapacity = freeSectionCapacityOfRailCarriages.get(railCarriageId);

        for (int i = inStation; i < outStation; i++) {
            int newCapacityOfSection = freeSectionCapacity.get(i) + 1;
            freeSectionCapacity.put(i, newCapacityOfSection);
        }

        return railCarriageId;
    }
}
