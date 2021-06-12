package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.FloorStats;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.*;

public class LoadBalancingElevatorController extends BaseElevatorController implements ElevatorController {

    public LoadBalancingElevatorController(final BuildingDimensions buildingDimensions) {
        super(buildingDimensions);
    }

    @Override
    public void program(final FloorStats floorStats, final Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus) {
        final List<ProgrammableElevator> idleElevators = elevatorsByIdleStatus.getOrDefault(true, Collections.emptyList());
        final SortedSet<Integer> nonEmptyFloors = floorStats.nonEmptyFloors();

        completeTripToSelectedFloors(idleElevators);
        handleGoingUp(floorStats, idleElevators, nonEmptyFloors);
        handleGoingDown(floorStats, idleElevators, nonEmptyFloors);
        stayCloseToOccupiedFloors(idleElevators, nonEmptyFloors);
    }

    private void completeTripToSelectedFloors(final List<ProgrammableElevator> idleElevators) {
        for (final Iterator<ProgrammableElevator> iterator = idleElevators.iterator(); iterator.hasNext(); ) {
            final ProgrammableElevator idleElevator = iterator.next();
            if (!idleElevator.getButtonsPressedInCar().isEmpty()) {
                overwriteProgramWithExistingStopsAndButtonsPressed(idleElevator);
                iterator.remove();
            }
        }
    }

    private void stayCloseToOccupiedFloors(final List<ProgrammableElevator> idleElevators,
                                           final SortedSet<Integer> nonEmptyFloors) {
        for (final Integer floorIndex : nonEmptyFloors) {
            final Optional<ProgrammableElevator> closest = idleElevators.stream()
                    .min(Comparator.comparingDouble(e -> e.distanceFrom(floorIndex)));
            closest.ifPresent(elevator -> {
                elevator.overwriteProgramStoppingOn(
                        sortedSet(Collections.singleton(floorIndex), elevator.isAboveFloor(floorIndex)));
                idleElevators.remove(elevator);
            });
        }
    }

    private void handleGoingDown(final FloorStats floorStats,
                                 final List<ProgrammableElevator> idleElevators,
                                 final SortedSet<Integer> nonEmptyFloors) {
        final SortedSet<Integer> waitingToGoDown = floorStats.waitingToGoDown();
        for (final Integer floorIndex : waitingToGoDown) {
            for (int i = 0; i < predictDemandFloor(floorStats, floorIndex, Attitude.NEGATIVE); i++) {
                sendClosestElevatorTo(floorIndex, idleElevators, nonEmptyFloors);
            }
        }
    }

    private void handleGoingUp(final FloorStats floorStats,
                               final List<ProgrammableElevator> idleElevators,
                               final SortedSet<Integer> nonEmptyFloors) {
        final SortedSet<Integer> waitingToGoUp = floorStats.waitingToGoUp();
        for (final Integer floorIndex : waitingToGoUp) {
            for (int i = 0; i < predictDemandFloor(floorStats, floorIndex, Attitude.POSITIVE); i++) {
                sendClosestElevatorTo(floorIndex, idleElevators, nonEmptyFloors);
            }
        }
    }

    private int predictDemandFloor(final FloorStats floorStats, final int floorIndex, final Attitude attitude) {
        final Integer value = floorStats.getStats().get(floorIndex).getOrDefault(attitude, 0);
        if (value == 0) {
            return 0;
        } else {
            return Math.max(1, value / getElevatorMaxOccupants());
        }
    }

    private void sendClosestElevatorTo(final int floorIndex,
                                       final List<ProgrammableElevator> idleElevators,
                                       final SortedSet<Integer> nonEmptyFloors) {
        final Optional<ProgrammableElevator> closest = idleElevators.stream()
                .min(Comparator.comparingDouble(e -> e.distanceFrom(floorIndex)));
        closest.ifPresent(elevator -> {
            elevator.overwriteProgramStoppingOn(sortedSet(Collections.singleton(floorIndex), elevator.isAboveFloor(floorIndex)));
            idleElevators.remove(elevator);
            nonEmptyFloors.remove(floorIndex);
        });
    }
}
