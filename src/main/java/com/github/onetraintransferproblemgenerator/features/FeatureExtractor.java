package com.github.onetraintransferproblemgenerator.features;

import com.github.onetraintransferproblemgenerator.models.*;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriageDistance;
import com.github.onetraintransferproblemgenerator.solvers.RailCarriagePositionHelper;
import com.github.onetraintransferproblemgenerator.solvers.SeatReservationStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FeatureExtractor {

    /**
     * Extracts features into already existing description
     */
    public static void extract(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        setDescriptionFields(description, problem);
    }

    /**
     * Extracts new instance feature description
     */
    public static InstanceFeatureDescription extract(String instanceId, OneTrainTransferProblem problem) {
        InstanceFeatureDescription description = new InstanceFeatureDescription();
        description.setInstanceId(instanceId);
        setDescriptionFields(description, problem);
        return description;
    }

    private static void setDescriptionFields(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        setPassengerFeatures(description, problem);
        description.setDirectionChangeCount(getDirectionChangeCount(problem));
        setRailCarriageCapacities(description, problem);
        setCongestion(description, problem);
        description.setBlockedPassengerRatio(getBlockedPassengerRatio(problem));
    }

    private static int getDirectionChangeCount(OneTrainTransferProblem problem) {
        DirectionOfTravel tmpDirection = null;
        int directionChangeCount = 0;
        for (DirectionOfTravel travelDirection : problem.getTrain().getStations().stream()
            .map(t -> t.getStationOperation().getTravelDirection()).toList()) {
            if (tmpDirection == null) {
                tmpDirection = travelDirection;
            } else if (!travelDirection.equals(tmpDirection)) {
                tmpDirection = travelDirection;
                directionChangeCount += 1;
            }
        }
        return directionChangeCount;
    }

    private static void setPassengerFeatures(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        description.setTotalPassengerCount(getTotalPassengerCount(problem));
        description.setPassengerRatio(getPassengerRatio(problem));
        description.setAvgPassengerRouteLength(getAveragePassengerRouteLength(problem));
    }

    private static int getTotalPassengerCount(OneTrainTransferProblem problem) {
        return problem.getPassengers().size();
    }

    private static double getPassengerRatio(OneTrainTransferProblem problem) {
        int stations = getStationCount(problem) - 1;
        int totalCapacity = stations *  problem.getTrain().getTotalCapacity();
        return (double) getTotalPassengerCount(problem) / totalCapacity;
    }

    private static double getAveragePassengerRouteLength(OneTrainTransferProblem problem) {
        int routeLengthSum = problem.getPassengers().stream()
                .map(p -> p.getOutStation() - p.getInStation())
                .reduce(Integer::sum)
                .orElse(0);
        double avgPassengerRouteLength = (double) routeLengthSum / problem.getPassengers().size();
        return Double.isNaN(avgPassengerRouteLength) ? 0.0 : avgPassengerRouteLength;
    }

    private static int getStationCount(OneTrainTransferProblem problem) {
        return problem.getTrain().getStations().size();
    }

    private static void setRailCarriageCapacities(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        double avgRailCarriageCapacity = getAverageRailCarriageCapacity(problem);
        description.setAvgRailCarriageCapacity(avgRailCarriageCapacity);
        description.setStdDevRailCarriageCapacity(getStandardDeviationRailCarriageCapacity(problem, avgRailCarriageCapacity));
    }

    private static double getAverageRailCarriageCapacity(OneTrainTransferProblem problem) {
        return problem.getTrain().getRailCarriages().stream()
            .map(RailCarriage::getCapacity)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }

    private static double getStandardDeviationRailCarriageCapacity(OneTrainTransferProblem problem, double mean) {
        double variance = problem.getTrain().getRailCarriages().stream()
                .map(rC -> Math.pow(rC.getCapacity() - mean, 2))
                .reduce(Double::sum)
                .orElse(0.0) / problem.getTrain().getRailCarriages().size();
        variance = Double.isNaN(variance) ? 0 : variance;
        return Math.sqrt(variance);
    }

    private static void setCongestion(InstanceFeatureDescription description, OneTrainTransferProblem problem) {
        description.setTotalCongestion(getTotalCongestion(problem));
    }

    private static double getTotalCongestion(OneTrainTransferProblem problem) {
        int availableSections = problem.getTrain().getTotalCapacity() * (problem.getTrain().getStationCount() - 1);
        int usedSections = problem.getPassengers().stream()
            .map(passenger -> passenger.getOutStation() - passenger.getInStation())
            .reduce(Integer::sum)
            .orElse(0);
        return (double) usedSections / availableSections;
    }

    private static double getBlockedPassengerRatio(OneTrainTransferProblem problem) {
        int totalPassengers = problem.getPassengers().size();
        double blockedPassengers = 0.0;

        RailCarriagePositionHelper railCarriagePositionHelper = new RailCarriagePositionHelper(problem.getTrain());
        SeatReservationStorage capacityStorage = new SeatReservationStorage(problem.getTrain());

        List<Integer> stationIds = problem.getTrain().getStationIds();

        for (int stationId : stationIds) {
            letPassengersOutOfTrain(capacityStorage, problem.getOutPassengersOfStation(stationId));
            blockedPassengers += seatPassengersInTrain(capacityStorage, railCarriagePositionHelper, problem.getInPassengersOfStation(stationId));
        }

        return blockedPassengers / totalPassengers;
    }

    private static void letPassengersOutOfTrain(SeatReservationStorage capacityStorage,
                                                List<Passenger> passengers) {
        passengers.forEach(passenger -> {
            try {
                capacityStorage.outPassenger(passenger);
            } catch (NullPointerException ignored) {

            }
        });
    }

    private static int seatPassengersInTrain(SeatReservationStorage capacityStorage,
                                       RailCarriagePositionHelper railCarriagePositionHelper,
                                       List<Passenger> passengers) {
        int blockedPassengers = 0;

        for (Passenger passenger : passengers) {
            List<RailCarriageDistance> railCarriageDistances = railCarriagePositionHelper.getDistancesForRailCarriages(passenger);
            railCarriageDistances.sort(Comparator.comparing(RailCarriageDistance::getCost));
            for (RailCarriageDistance railCarriageDistance : railCarriageDistances) {
                if (capacityStorage.isBoardingPossible(railCarriageDistance.getRailCarriageId())) {
                    capacityStorage.inPassenger(railCarriageDistance.getRailCarriageId(), passenger);
                } else {
                    blockedPassengers++;
                }
                break;
            }
        }
        return blockedPassengers;
    }

}
