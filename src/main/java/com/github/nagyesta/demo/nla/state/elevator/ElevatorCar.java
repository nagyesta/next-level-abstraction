package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.state.building.AbstractPlace;
import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.building.Building;
import com.github.nagyesta.demo.nla.state.people.Person;

import java.util.*;
import java.util.stream.Collectors;

public class ElevatorCar extends AbstractPlace implements MutableElevator {

    public static final double SLOW_TURNS_TO_FAST_TURNS = 0.5D;
    public static final int DISTANCE_TO_TURNS_MOVING_SLOW = 2;
    public static final double SAFE_SLOW_DOWN_DISTANCE = 0.5D;
    public static final double SAFE_SPEED_UP_DISTANCE = 0.5D;
    public static final double SLOW_ONLY_DISTANCE = SAFE_SPEED_UP_DISTANCE * 2 + SAFE_SLOW_DOWN_DISTANCE;

    private final SortedSet<Integer> buttonsPressedInCar;
    private final Set<Integer> plannedStops;
    private final ElevatorStepCounter stepCounter;
    private final ElevatorProgramSupport elevatorProgramSupport;
    private ElevatorStatus status;
    private boolean justClosedDoor = false;

    public ElevatorCar(final Building building, final int index, final String name, final int totalCapacity) {
        super(building, index, name, 0, totalCapacity);
        this.buttonsPressedInCar = new TreeSet<>();
        this.plannedStops = new TreeSet<>();
        this.status = ElevatorStatus.STOPPED;
        this.stepCounter = new ElevatorStepCounter();
        this.elevatorProgramSupport = new ElevatorProgramSupport(building.getBuildingDimensions());
    }

    @Override
    public void accept(final Person person) {
        super.accept(person);
        this.buttonsPressedInCar.addAll(person.pressButton());
    }

    @Override
    public boolean isIdle() {
        return elevatorProgramSupport.isIdle();
    }

    @Override
    public SortedSet<Integer> getButtonsPressedInCar() {
        return Collections.unmodifiableSortedSet(buttonsPressedInCar);
    }

    @Override
    public Set<Integer> getPlannedStops() {
        return Collections.unmodifiableSet(plannedStops);
    }

    @Override
    public ElevatorStepCounter getStepCounter() {
        return stepCounter;
    }

    @Override
    public boolean canGoFast(final double fromFloor, final int toFloor) {
        return elevatorProgramSupport.calculateDistance(fromFloor, toFloor) > SLOW_ONLY_DISTANCE;
    }

    @Override
    public void overwriteProgramStoppingOn(final SortedSet<Integer> floors) {
        if (!Objects.requireNonNull(floors).isEmpty() && !canSafelyStopOnFloor(floors.first())) {
            final String set = floors.stream()
                    .map(getBuilding()::floor)
                    .map(AbstractPlace::getName)
                    .map(String::trim)
                    .collect(Collectors.joining(", "));
            final String going = plannedStops.stream()
                    .map(getBuilding()::floor)
                    .map(AbstractPlace::getName)
                    .map(String::trim)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Elevator " + getName() + " - Cannot stop on floor "
                    + set + ", as destinations floors are " + going + ". Keeping program as-is.");
        }
        plannedStops.clear();
        if (floors.isEmpty()) {
            elevatorProgramSupport.programTravelToFloor(this, getFloorIndex(), this.getFloorIndex());
        } else {
            elevatorProgramSupport.generateProgramForDestinations(this, floors);
            plannedStops.addAll(floors);
        }
    }

    @Override
    public void removeButtonPressOnArrival() {
        this.buttonsPressedInCar.remove((int) getFloorIndex());
        this.plannedStops.remove((int) getFloorIndex());
        this.justClosedDoor = true;
    }

    @Override
    public void setFloorIndex(final double floorIndex) {
        super.setFloorIndex(floorIndex);
    }

    @Override
    public void nextTurn(final int turnIndex) {
        this.population().forEach(p -> p.nextTurn(turnIndex));
        elevatorProgramSupport.nextTurn(this);
    }

    @Override
    public void setAttitude(final Attitude attitude) {
        super.setAttitude(attitude);
    }

    @Override
    public ElevatorStatus getStatus() {
        return status;
    }

    public void setStatus(final ElevatorStatus elevatorStatus) {
        this.status = elevatorStatus;
        this.justClosedDoor = false;
    }

    @Override
    public boolean justClosedDoor() {
        return justClosedDoor;
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public boolean canSafelyStopOnFloor(final int floorIndex) {
        final Attitude movementNeeded = Attitude.forMovement(getFloorIndex(), floorIndex);
        if (oppositeDirection(movementNeeded)) {
            return false;
        } else if (!status.isMoving()) {
            return true;
        } else if (status.isMovingSlow()) {
            return !hasDoorOnFloor(floorIndex);
        } else {
            return elevatorProgramSupport.calculateDistance(getFloorIndex(), floorIndex) > SAFE_SLOW_DOWN_DISTANCE;
        }
    }

    @Override
    public boolean hasDoorOnFloor(final int floorIndex) {
        return elevatorProgramSupport.hasDoorOnFloor(getFloorIndex(), floorIndex);
    }

    @Override
    public boolean isAboveFloor(final int floorIndex) {
        return floorIndex < getFloorIndex();
    }

    @Override
    public boolean isBelowFloor(final int floorIndex) {
        return floorIndex > getFloorIndex();
    }

    private boolean oppositeDirection(final Attitude movementNeeded) {
        return getAttitude() != Attitude.CONSTANT
                && movementNeeded != Attitude.CONSTANT
                && getAttitude() != movementNeeded;
    }

}
