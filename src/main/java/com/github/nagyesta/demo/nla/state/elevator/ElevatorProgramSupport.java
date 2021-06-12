package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.Floor;
import com.github.nagyesta.demo.nla.state.program.Step;

import java.util.*;
import java.util.function.BiConsumer;

import static com.github.nagyesta.demo.nla.state.elevator.ElevatorCar.*;

public class ElevatorProgramSupport {

    private final Deque<Step<ElevatorStatus>> steps = new LinkedList<>();
    private final BuildingDimensions buildingDimensions;

    public ElevatorProgramSupport(final BuildingDimensions buildingDimensions) {
        this.buildingDimensions = Objects.requireNonNull(buildingDimensions);
    }

    public boolean isIdle() {
        return this.steps.isEmpty();
    }

    public boolean hasDoorOnFloor(final double currentFloorIndex, final double targetFloorIndex) {
        return targetFloorIndex == currentFloorIndex;
    }

    public boolean isNotOnFloor(final double currentFloorIndex, final double targetFloorIndex) {
        return !hasDoorOnFloor(currentFloorIndex, targetFloorIndex);
    }

    public double calculateDistance(final double fromFloor, final double toFloor) {
        return Math.abs(toFloor - fromFloor);
    }

    public void nextTurn(final MutableElevator elevator) {
        if (steps.isEmpty()) {
            elevator.setAttitude(Attitude.CONSTANT);
            if (isDoorOpenInLastStep(elevator.getStatus())) {
                onDoorOpenStep(elevator, null);
            }
        } else {
            elevator.setStatus(takeNextStep(elevator, steps.element()));
        }
    }

    public void generateProgramForDestinations(final MutableElevator elevator, final SortedSet<Integer> floors) {
        this.steps.clear();
        Optional<Double> fromFloor = Optional.empty();
        for (final Integer floor : floors) {
            doProgramTravelToFloor(elevator, fromFloor.orElse(elevator.getFloorIndex()), buildingDimensions.requireValidFloorIndex(floor));
            fromFloor = Optional.of((double) floor);
        }
    }

    public void programTravelToFloor(final MutableElevator elevator, final double fromFloor, final double toFloor) {
        if (buildingDimensions.requireValidFloorIndex(roundToInt(toFloor)) != toFloor) {
            throw new IllegalArgumentException("Cannot travel to half-floors.");
        }
        this.steps.clear();
        doProgramTravelToFloor(elevator, fromFloor, toFloor);
    }

    private ElevatorStatus takeNextStep(final MutableElevator elevator, final Step<ElevatorStatus> nextStep) {
        final ElevatorStatus action = Objects.requireNonNull(nextStep).getAction();
        if (action == ElevatorStatus.STOPPED) {
            takeAction(elevator, this::noopStep);
        } else if (action == ElevatorStatus.DOOR_OPEN) {
            takeAction(elevator, this::onDoorOpenStep);
        } else {
            takeAction(elevator, this::onMoveStep);
        }
        return Optional.ofNullable(steps.peek()).orElse(nextStep).getAction();
    }

    private void takeAction(final MutableElevator elevator, final BiConsumer<MutableElevator, Step<ElevatorStatus>> consumer) {
        consumer.accept(elevator, steps.remove());
        if (steps.isEmpty()) {
            elevator.setAttitude(Attitude.CONSTANT);
        }
    }

    private void noopStep(final MutableElevator elevator, final Step<ElevatorStatus> step) {
    }

    private void onMoveStep(final MutableElevator elevator, final Step<ElevatorStatus> step) {
        elevator.getStepCounter().recordMove(step);
        elevator.setFloorIndex(elevator.getFloorIndex() + step.getAction().getTravelPerTurn());
    }

    private void onDoorOpenStep(final MutableElevator elevator, final Step<ElevatorStatus> step) {
        Optional.ofNullable(step).ifPresent(s -> elevator.getStepCounter().recordDoorOpen());
        final Floor floor = elevator.getBuilding().floor(roundToInt(elevator.getFloorIndex()));
        elevator.population().forEach(p -> p.arriveTo(floor));
        elevator.removeButtonPressOnArrival();
        floor.population().forEach(p -> {
            p.elevatorArrived(elevator);
            if (elevator.population().isEmpty()) {
                elevator.setAttitude(Attitude.forMovement(elevator.getFloorIndex(), p.nextDestination()));
            }
        });
    }

    private void doProgramTravelToFloor(final MutableElevator elevator, final double fromFloor, final double toFloor) {
        if (hasDoorOnFloor(fromFloor, toFloor) && isDoorOpenInLastStep(elevator.getStatus())) {
            elevator.setAttitude(Attitude.CONSTANT);
        } else if (isNotOnFloor(fromFloor, toFloor)) {
            addTripSteps(elevator, fromFloor, toFloor);
            elevator.setAttitude(Attitude.forMovement(fromFloor, toFloor));
        } else {
            addOpenDoorStep();
            elevator.setAttitude(Attitude.CONSTANT);
        }
    }

    private boolean isDoorOpenInLastStep(final ElevatorStatus currentStatus) {
        return Optional.ofNullable(steps.peekLast())
                .map(Step::getAction)
                .orElse(currentStatus).isDoorOpen();
    }

    private void addTripSteps(final Elevator elevator, final double fromFloor, final double toFloor) {
        addCloseDoorStepIfNeeded(elevator);
        addStepsToMove(elevator, fromFloor, toFloor);
        addStopStep();
        addOpenDoorStep();
    }

    private void addCloseDoorStepIfNeeded(final Elevator elevator) {
        if (isDoorOpenInLastStep(elevator.getStatus())) {
            addStopStep();
        }
    }

    private void addStepsToMove(final Elevator elevator, final double fromFloor, final double toFloor) {
        final Attitude attitude = Attitude.forMovement(fromFloor, toFloor);
        final int slowTurnsTotal = distanceToTurns(calculateDistance(fromFloor, toFloor));
        if (elevator.canGoFast(fromFloor, roundToInt(toFloor))) {
            addFastMovementFor(slowTurnsTotal, attitude);
        } else {
            addSlowMovementFor(slowTurnsTotal, attitude);
        }
    }

    private void addStopStep() {
        this.steps.add(new Step<>(ElevatorStatus.STOPPED));
    }

    private void addOpenDoorStep() {
        this.steps.add(new Step<>(ElevatorStatus.DOOR_OPEN));
    }

    private void addFastMovementFor(final int slowTurnsTotal, final Attitude attitude) {
        final int slowStartTurns = distanceToTurns(SAFE_SPEED_UP_DISTANCE);
        final int slowStopTurns = distanceToTurns(SAFE_SLOW_DOWN_DISTANCE) + slowTurnsTotal % DISTANCE_TO_TURNS_MOVING_SLOW;
        final int fastTurns = roundToInt((slowTurnsTotal - slowStartTurns - slowStopTurns) * SLOW_TURNS_TO_FAST_TURNS);
        addSlowMovementFor(slowStartTurns, attitude);
        addMultiple(attitude.fastMove(), fastTurns);
        addSlowMovementFor(slowStopTurns, attitude);
    }

    private void addSlowMovementFor(final int slowTurnsTotal, final Attitude attitude) {
        addMultiple(attitude.slowMove(), slowTurnsTotal);
    }

    private void addMultiple(final ElevatorStatus status, final int turns) {
        for (int i = 0; i < turns; i++) {
            this.steps.add(new Step<>(status));
        }
    }

    private int distanceToTurns(final double distance) {
        return roundToInt(distance * DISTANCE_TO_TURNS_MOVING_SLOW);
    }

    private int roundToInt(final double d) {
        return (int) Math.round(d);
    }

}
