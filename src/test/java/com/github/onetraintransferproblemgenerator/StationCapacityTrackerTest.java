package com.github.onetraintransferproblemgenerator;

import com.github.onetraintransferproblemgenerator.controller.generation.expand.StationCapacityTracker;
import com.github.onetraintransferproblemgenerator.helpers.Tuple;
import com.github.onetraintransferproblemgenerator.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StationCapacityTrackerTest {

    private static Train train;

    @BeforeAll
    static void initializeTrain() {
        train = new Train();
        List<StationTuple> stations = new ArrayList<>(List.of(
            new StationTuple(1, null),
            new StationTuple(2, null),
            new StationTuple(3, null),
            new StationTuple(4, null)));
        train.setStations(stations);
        List<RailCarriage> railCarriages = new ArrayList<>(List.of(new RailCarriage(1, 1)));
        train.setRailCarriages(railCarriages);
    }

    @Test
    void EmptyTrain_RideFromStartToEnd() {
        List<Passenger> passengers = new ArrayList<>();
        OneTrainTransferProblem problem = new OneTrainTransferProblem(train, passengers);
        StationCapacityTracker capacityTracker = new StationCapacityTracker(problem);

        List<Tuple<Integer, Integer>> availableRides = capacityTracker.getAvailableRides();

        assertEquals(1, availableRides.get(0).getLeft());
        assertEquals(4, availableRides.get(0).getRight());
    }

    @Test
    void PassengerInMiddle_2Rides() {
        List<Passenger> passengers = new ArrayList<>();
        Passenger passenger = new Passenger();
        passenger.setInStation(2);
        passenger.setOutStation(3);
        passengers.add(passenger);
        OneTrainTransferProblem problem = new OneTrainTransferProblem(train, passengers);
        StationCapacityTracker capacityTracker = new StationCapacityTracker(problem);

        List<Tuple<Integer, Integer>> availableRides = capacityTracker.getAvailableRides();

        assertEquals(2, availableRides.size());
        //First Ride
        assertEquals(1, availableRides.get(0).getLeft());
        assertEquals(2, availableRides.get(0).getRight());
        //Second Ride
        assertEquals(3, availableRides.get(1).getLeft());
        assertEquals(4, availableRides.get(1).getRight());
    }

    @Test
    void FullTrain_NoAvailableRide() {
        List<Passenger> passengers = new ArrayList<>();
        Passenger passenger = new Passenger();
        passenger.setInStation(1);
        passenger.setOutStation(4);
        passengers.add(passenger);
        OneTrainTransferProblem problem = new OneTrainTransferProblem(train, passengers);
        StationCapacityTracker capacityTracker = new StationCapacityTracker(problem);

        List<Tuple<Integer, Integer>> availableRides = capacityTracker.getAvailableRides();

        assertEquals(0, availableRides.size());
    }
}
