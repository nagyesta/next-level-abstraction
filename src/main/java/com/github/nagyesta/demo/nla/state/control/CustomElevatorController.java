package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.*;
import java.util.function.Consumer;

public class CustomElevatorController extends DirectionAwareElevatorController implements ElevatorController {

    public CustomElevatorController(final BuildingDimensions buildingDimensions) {
        super(buildingDimensions);
    }

    @Override
    protected void doProgram(final SortedSet<Integer> waitingToGoUp,
                             final SortedSet<Integer> waitingToGoDown,
                             final Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus) {
        final List<ProgrammableElevator> busy = elevatorsByIdleStatus.getOrDefault(false, Collections.emptyList());
        final List<ProgrammableElevator> idle = elevatorsByIdleStatus.getOrDefault(true, Collections.emptyList());
        idle.forEach(doProgramIdle(waitingToGoUp, waitingToGoDown, idle.size() + busy.size()));
        busy.forEach(doProgramBusy(waitingToGoUp, waitingToGoDown, idle.size() + busy.size()));
    }

    protected Consumer<ProgrammableElevator> doProgramBusy(
            final SortedSet<Integer> waitingToGoUp,
            final SortedSet<Integer> waitingToGoDown,
            final int totalElevatorCount) {
        return elevator -> {
            final SortedSet<Integer> pressedInCar = elevator.getButtonsPressedInCar();
            if (!pressedInCar.isEmpty()) {
                if (pressedInCar.stream().allMatch(elevator::isAboveFloor)) {
                    final Optional<Integer> topFloorWaiting = waitingToGoDown.stream()
                            .filter(elevator::isAboveFloor)
                            .filter(elevator::canSafelyStopOnFloor)
                            .max(Comparator.naturalOrder());
                    final TreeSet<Integer> destinations = new TreeSet<>(elevator.getButtonsPressedInCar());
                    topFloorWaiting.ifPresent(destinations::add);
                    elevator.overwriteProgramStoppingOn(
                            sortedSet(destinations, true));
                } else if (pressedInCar.stream().allMatch(elevator::isBelowFloor)) {
                    final Optional<Integer> bottomFloorWaiting = Optional.empty();
                    final TreeSet<Integer> destinations = new TreeSet<>(elevator.getButtonsPressedInCar());
                    bottomFloorWaiting.ifPresent(destinations::add);
                    elevator.overwriteProgramStoppingOn(
                            sortedSet(destinations, false));
                }
            }
        };
    }

    protected Consumer<ProgrammableElevator> doProgramIdle(
            final SortedSet<Integer> waitingToGoUp,
            final SortedSet<Integer> waitingToGoDown,
            final int totalElevatorCount) {
        return elevator -> {
            final SortedSet<Integer> pressedInCar = elevator.getButtonsPressedInCar();
            final Set<Integer> floorsWaiting = new HashSet<>(waitingToGoDown);
            floorsWaiting.addAll(waitingToGoUp);
            if (!pressedInCar.isEmpty()) {
                if (pressedInCar.stream().allMatch(elevator::isAboveFloor)) {
                    elevator.overwriteProgramStoppingOn(
                            sortedSet(elevator.getButtonsPressedInCar(), true));
                } else {
                    elevator.overwriteProgramStoppingOn(
                            sortedSet(elevator.getButtonsPressedInCar(), false));
                }
            } else if (!floorsWaiting.isEmpty()) {
                final Optional<Integer> closestFloor = floorsWaiting.stream()
                        .min(Comparator.comparing(elevator::distanceFrom));
                closestFloor.ifPresent(integer ->
                        elevator.overwriteProgramStoppingOn(
                                sortedSet(Collections.singleton(integer), false)));
            } else {
                final int waitDestination = (getTopFloorIndex() * elevator.getIndex()) / totalElevatorCount;
                elevator.overwriteProgramStoppingOn(
                        sortedSet(Collections.singleton(waitDestination), false));
            }
        };
    }

}
