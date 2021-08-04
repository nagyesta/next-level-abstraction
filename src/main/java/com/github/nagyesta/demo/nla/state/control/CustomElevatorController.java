package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.FloorStats;
import com.github.nagyesta.demo.nla.state.elevator.MutableElevator;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;
import com.github.nagyesta.demo.nla.state.people.GenericPerson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CustomElevatorController extends BaseElevatorController implements ElevatorController {

    public CustomElevatorController(final BuildingDimensions buildingDimensions) {
        super(buildingDimensions);
    }

    @Override
    public void program(final FloorStats floorStats,
                        final Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus) {

        final SortedSet<Integer> waitingUp = floorStats.waitingToGoUp();
        final SortedSet<Integer> waitingDown = floorStats.waitingToGoDown();
        final List<ProgrammableElevator> busy = elevatorsByIdleStatus.getOrDefault(false, Collections.emptyList());
        final List<ProgrammableElevator> idle = elevatorsByIdleStatus.getOrDefault(true, Collections.emptyList());
        busy.forEach(doProgramBusy(waitingUp, waitingDown));
        idle.forEach(doProgramIdle(waitingUp, waitingDown));
    }

    protected Consumer<ProgrammableElevator> doProgramIdle(final SortedSet<Integer> waitingUp,
                                                           final SortedSet<Integer> waitingDown) {
        return elevator -> {
            final SortedSet<Integer> floorsWaiting = sortedSet(waitingDown, false);
            floorsWaiting.addAll(waitingUp);
            final SortedSet<Integer> buttonsPressedInCar = elevator.getButtonsPressedInCar();
            if (elevator.getButtonsPressedInCar().isEmpty()) {
                if (floorsWaiting.isEmpty()) {
                    elevator.overwriteProgramStoppingOn(toGroundFast());
                } else if (elevator.isBelowFloor(floorsWaiting.last())) {
                    elevator.overwriteProgramStoppingOn(sortedSingleton(floorsWaiting.last()));
                } else {
                    elevator.overwriteProgramStoppingOn(sortedSingleton(floorsWaiting.first()));
                }
            } else {
                if (buttonsPressedInCar.stream().allMatch(elevator::isBelowFloor)) {
                    elevator.overwriteProgramStoppingOn(sortedSet(buttonsPressedInCar, false));
                } else {
                    elevator.overwriteProgramStoppingOn(sortedSet(buttonsPressedInCar, true));
                }
            }
        };
    }

    private SortedSet<Integer> sortedSingleton(final Integer singleton) {
        return sortedSet(Collections.singleton(singleton), false);
    }

    protected Consumer<ProgrammableElevator> doProgramBusy(final SortedSet<Integer> waitingUp,
                                                           final SortedSet<Integer> waitingDown) {
        return elevator -> {
            final SortedSet<Integer> buttonsPressedInCar = elevator.getButtonsPressedInCar();
            if (buttonsPressedInCar.stream().anyMatch(elevator::hasDoorOnFloor)) {
                if (!elevator.getStatus().isMoving()) {
                    ((MutableElevator) elevator).removeButtonPressOnArrival();
                }
                return;
            }
            if (elevator.getFreeCapacity() < 3.9 * GenericPerson.AVERAGE_CAPACITY_TAKEN) {
                return;
            }
            if (!elevator.getStatus().isMoving()) {
                return;
            }
            if (elevator.getAttitude() == Attitude.POSITIVE) {
                final Optional<Integer> lowestSafeStop = waitingUp.stream()
                        .filter(elevator::isBelowFloor)
                        .filter(elevator::canSafelyStopOnFloor)
                        .min(Comparator.naturalOrder());
                if (lowestSafeStop.isPresent()) {
                    final SortedSet<Integer> destinations = sortedSet(buttonsPressedInCar.stream()
                            .filter(elevator::isBelowFloor)
                            .collect(Collectors.toSet()), false);
                    destinations.add(lowestSafeStop.get());
                    elevator.overwriteProgramStoppingOn(destinations);
                    waitingUp.remove(lowestSafeStop.get());
                }
            } else if (elevator.getAttitude() == Attitude.NEGATIVE) {
                final Optional<Integer> highestSafeStop = waitingDown.stream()
                        .filter(elevator::isAboveFloor)
                        .filter(elevator::canSafelyStopOnFloor)
                        .max(Comparator.naturalOrder());
                if (highestSafeStop.isPresent()) {
                    final SortedSet<Integer> destinations = sortedSet(buttonsPressedInCar.stream()
                            .filter(elevator::isAboveFloor)
                            .collect(Collectors.toSet()), true);
                    destinations.add(highestSafeStop.get());
                    elevator.overwriteProgramStoppingOn(destinations);
                    waitingDown.remove(highestSafeStop.get());
                }
            } else {
                if (!buttonsPressedInCar.isEmpty()) {
                    elevator.overwriteProgramStoppingOn(buttonsPressedInCar);
                } else {
                    elevator.overwriteProgramStoppingOn(sortedSingleton(0));
                }
            }
        };
    }

}
