package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseElevatorController implements ElevatorController {

    private final BuildingDimensions buildingDimensions;

    public BaseElevatorController(final BuildingDimensions buildingDimensions) {
        this.buildingDimensions = Objects.requireNonNull(buildingDimensions);
    }

    protected int getTopFloorIndex() {
        return buildingDimensions.getFloors() - 1;
    }

    protected int getElevatorMaxOccupants() {
        return buildingDimensions.getElevatorMaxOccupants();
    }

    protected SortedSet<Integer> allStopsUp() {
        return IntStream.rangeClosed(0, getTopFloorIndex()).boxed()
                .collect(Collectors.toCollection(TreeSet::new));
    }

    protected SortedSet<Integer> allStopsDown() {
        return sortedSet(allStopsUp(), true);
    }

    protected SortedSet<Integer> toGroundFast() {
        return new TreeSet<>(Collections.singleton(0));
    }

    protected SortedSet<Integer> toTopFast() {
        return new TreeSet<>(Collections.singleton(getTopFloorIndex()));
    }

    protected SortedSet<Integer> sortedSet(final Collection<Integer> destinations, final boolean reverse) {
        final SortedSet<Integer> result;
        if (reverse) {
            result = new TreeSet<>(Comparator.reverseOrder());
        } else {
            result = new TreeSet<>();
        }
        result.addAll(destinations);
        return result;
    }

    protected void overwriteProgramWithExistingStopsAndButtonsPressed(final ProgrammableElevator elevator) {
        final boolean reverse = elevator.getButtonsPressedInCar().stream().noneMatch(elevator::isBelowFloor);
        final SortedSet<Integer> buttonsPressed = sortedSet(elevator.getButtonsPressedInCar(), reverse);
        buttonsPressed.remove(elevatorFloorIndex(elevator));
        elevator.overwriteProgramStoppingOn(buttonsPressed);
    }

    protected int elevatorFloorIndex(final ProgrammableElevator elevator) {
        return (int) elevator.getFloorIndex();
    }
}
