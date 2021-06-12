package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.*;
import java.util.function.Consumer;

public class StopOnlyWhenPressedElevatorController extends DirectionAwareElevatorController implements ElevatorController {

    public StopOnlyWhenPressedElevatorController(final BuildingDimensions buildingDimensions) {
        super(buildingDimensions);
    }

    @Override
    protected void doProgram(final SortedSet<Integer> waitingToGoUp,
                             final SortedSet<Integer> waitingToGoDown,
                             final Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus) {
        elevatorsByIdleStatus.getOrDefault(true, Collections.emptyList())
                .forEach(doProgram(waitingToGoUp, waitingToGoDown));
    }

    protected Consumer<ProgrammableElevator> doProgram(final SortedSet<Integer> up, final SortedSet<Integer> down) {
        return elevator -> {
            if (!elevator.getButtonsPressedInCar().isEmpty()) {
                final SortedSet<Integer> destinations;
                if (elevator.getButtonsPressedInCar().stream().noneMatch(elevator::isBelowFloor)) {
                    destinations = new TreeSet<>(Comparator.reverseOrder());
                } else {
                    destinations = new TreeSet<>();
                }
                destinations.addAll(elevator.getButtonsPressedInCar());
                elevator.overwriteProgramStoppingOn(destinations);
            } else {
                if (!up.isEmpty() && elevator.isBelowFloor(up.first())) {
                    elevator.overwriteProgramStoppingOn(up);
                } else if (!down.isEmpty() && elevator.isAboveFloor(down.first())) {
                    elevator.overwriteProgramStoppingOn(down);
                } else if (!up.isEmpty()) {
                    elevator.overwriteProgramStoppingOn(new TreeSet<>(Collections.singleton(up.first())));
                } else if (!down.isEmpty()) {
                    elevator.overwriteProgramStoppingOn(new TreeSet<>(Collections.singleton(down.first())));
                } else {
                    elevator.overwriteProgramStoppingOn(new TreeSet<>(Collections.singleton(0)));
                }
            }
        };
    }

}
