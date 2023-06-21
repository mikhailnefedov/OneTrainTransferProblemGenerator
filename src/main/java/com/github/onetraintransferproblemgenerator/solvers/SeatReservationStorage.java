package com.github.onetraintransferproblemgenerator.solvers;


import com.github.onetraintransferproblemgenerator.models.Passenger;
import com.github.onetraintransferproblemgenerator.models.RailCarriage;
import com.github.onetraintransferproblemgenerator.models.Train;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO: first developed helper for solvers, may be refactored to use newer helpers, because only works in order by going
//through each station sequentially
public class SeatReservationStorage {

    private final HashMap<Integer, Integer> freeCapacityOfRailCarriages;
    private final HashMap<Passenger, Integer> railCarriageOfPassenger;

    public SeatReservationStorage(Train train) {
        freeCapacityOfRailCarriages = new HashMap<>();
        railCarriageOfPassenger = new HashMap<>();

        for (RailCarriage railCarriage : train.getRailCarriages()) {
            freeCapacityOfRailCarriages.put(railCarriage.getSequenceNumber(), railCarriage.getCapacity());
        }
    }

    public int inPassenger(int railCarriageId, Passenger passenger) {
        int newCapacity = freeCapacityOfRailCarriages.get(railCarriageId) - 1;
        freeCapacityOfRailCarriages.put(railCarriageId, newCapacity);

        railCarriageOfPassenger.put(passenger, railCarriageId);

        return railCarriageId;
    }

    public boolean isBoardingPossible(int railCarriageId) {
        return freeCapacityOfRailCarriages.get(railCarriageId) > 0;
    }

    public int outPassenger(Passenger passenger) {
        int railCarriageId = railCarriageOfPassenger.get(passenger);

        int newCapacity = freeCapacityOfRailCarriages.get(railCarriageId) + 1;
        freeCapacityOfRailCarriages.put(railCarriageId, newCapacity);

        return railCarriageId;
    }

}
