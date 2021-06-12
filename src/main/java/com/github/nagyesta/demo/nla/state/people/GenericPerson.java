package com.github.nagyesta.demo.nla.state.people;

import com.github.nagyesta.demo.nla.annotation.ExcludeIdeGeneratedToStringFromJacoco;
import com.github.nagyesta.demo.nla.state.building.AbstractPlace;
import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.building.Floor;
import com.github.nagyesta.demo.nla.state.elevator.Elevator;
import com.github.nagyesta.demo.nla.state.program.Step;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;

public class GenericPerson implements Person {

    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    private final long id;
    private final StepEvaluator stepEvaluator;
    private final PersonStepCounter stepCounter;
    private final SpaceRequirements spaceRequirements;
    private final LocationHolder location;
    private final Queue<Step<Action>> steps;
    private int lastActionTime;

    public GenericPerson(final Queue<Step<Action>> steps,
                         final SpaceRequirements spaceRequirements) {
        this(SEQUENCE.incrementAndGet(), steps, spaceRequirements);
    }

    private GenericPerson(final long id, final Queue<Step<Action>> steps,
                          final SpaceRequirements spaceRequirements) {
        this.id = id;
        this.steps = new LinkedList<>(Objects.requireNonNull(steps));
        this.spaceRequirements = Objects.requireNonNull(spaceRequirements);
        this.stepCounter = new PersonStepCounter();
        this.stepEvaluator = new StepEvaluator();
        this.location = new LocationHolder();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int compareTo(final Person o) {
        return WAIT_SINCE_COMPARATOR.compare(this, o);
    }

    @Override
    public int getLastActionTime() {
        return lastActionTime;
    }

    @Override
    public boolean needsElevator() {
        return attitude() != Attitude.CONSTANT;
    }

    @Override
    public int nextDestination() {
        return Optional.ofNullable(steps.peek())
                .filter(stepEvaluator::shouldMove)
                .flatMap(Step::getDestination)
                .orElse((int) onFloor());
    }

    @Override
    public double onFloor() {
        return location.onFloor();
    }

    @Override
    public int getSpaceNeeded() {
        return spaceRequirements.getSpaceNeeded();
    }

    @Override
    public int getSpaceTaken() {
        return spaceRequirements.getSpaceTaken();
    }

    @Override
    public PersonStepCounter getStepCounter() {
        return stepCounter;
    }

    @Override
    public Attitude attitude() {
        if (onFloor() > nextDestination()) {
            return Attitude.NEGATIVE;
        } else if (onFloor() < nextDestination()) {
            return Attitude.POSITIVE;
        } else {
            return Attitude.CONSTANT;
        }
    }

    protected boolean wantsToEnter(final Floor place) {
        return nextDestination() == place.getFloorIndex();
    }

    protected boolean wantsToEnter(final Elevator place) {
        return needsElevator()
                && canGoToRightDirection(place);
    }

    private boolean canGoToRightDirection(final Elevator elevator) {
        return elevator.population().isEmpty()
                || elevator.population().stream().allMatch(p -> p.attitude() == attitude())
                || attitude() == elevator.getAttitude();
    }

    @Override
    public void arriveTo(final Floor floor) {
        if (wantsToEnter(floor)) {
            location.moveTo(floor, this);
        }
    }

    @Override
    public void elevatorArrived(final Elevator elevator) {
        if (wantsToEnter(elevator)) {
            location.moveTo((AbstractPlace) elevator, this);
        }
    }

    @Override
    public Set<Integer> pressButton() {
        return Collections.singleton(nextDestination());
    }

    @Override
    public void updateLocation(final AbstractPlace place) {
        location.updateLocation(this, place, spaceRequirements);
    }

    @Override
    public void nextTurn(final int turnIndex) {
        if (location.isInElevator()) {
            stepCounter.recordElevatorTravel();
        } else if (needsElevator()) {
            stepCounter.recordElevatorWait();
        } else {
            takeStep(turnIndex, steps.peek());
        }
    }

    @Override
    public boolean finished() {
        return steps.isEmpty();
    }

    private void takeStep(final int turnIndex, final Step<Action> nextStep) {
        if (stepEvaluator.wantsToLeave(nextStep)) {
            location.leave(this);
            takeAction(turnIndex);
        } else if (stepEvaluator.wantsToEnter(nextStep)) {
            location.enter(this);
            takeAction(turnIndex);
        } else if (stepEvaluator.shouldWait(nextStep) || stepEvaluator.destinationReached(this, nextStep)) {
            takeAction(turnIndex);
        }
    }

    private void takeAction(final int turnIndex) {
        steps.remove();
        lastActionTime = turnIndex;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenericPerson)) {
            return false;
        }
        final GenericPerson that = (GenericPerson) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    @ExcludeIdeGeneratedToStringFromJacoco
    public String toString() {
        return new StringJoiner(", ", GenericPerson.class.getSimpleName() + "[", "]\n")
                .add("id=" + id)
                .add("lastActionTime=" + lastActionTime)
                .add("location=" + location)
                .add("stepCounter=" + stepCounter)
                .add("steps=" + steps.toString())
                .toString();
    }
}
