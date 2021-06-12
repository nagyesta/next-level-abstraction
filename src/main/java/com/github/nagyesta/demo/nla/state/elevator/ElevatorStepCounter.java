package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.annotation.ExcludeIdeGeneratedToStringFromJacoco;
import com.github.nagyesta.demo.nla.state.program.Step;

import java.util.Objects;
import java.util.StringJoiner;

public class ElevatorStepCounter {

    private int doorOpened = 0;
    private int slowMoves = 0;
    private int fastMoves = 0;

    public int getDoorOpened() {
        return doorOpened;
    }

    public int getSlowMoves() {
        return slowMoves;
    }

    public int getFastMoves() {
        return fastMoves;
    }

    public void recordDoorOpen() {
        doorOpened++;
    }

    public void recordMove(final Step<ElevatorStatus> nextStep) {
        if (nextStep.getAction() == ElevatorStatus.MOVING_UP_FAST || nextStep.getAction() == ElevatorStatus.MOVING_DOWN_FAST) {
            recordFastMove();
        } else if (nextStep.getAction() == ElevatorStatus.MOVING_UP_SLOW || nextStep.getAction() == ElevatorStatus.MOVING_DOWN_SLOW) {
            recordSlowMove();
        }
    }

    private void recordFastMove() {
        fastMoves++;
    }

    private void recordSlowMove() {
        slowMoves++;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElevatorStepCounter)) {
            return false;
        }
        final ElevatorStepCounter that = (ElevatorStepCounter) o;
        return doorOpened == that.doorOpened && slowMoves == that.slowMoves && fastMoves == that.fastMoves;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doorOpened, slowMoves, fastMoves);
    }

    @Override
    @ExcludeIdeGeneratedToStringFromJacoco
    public String toString() {
        return new StringJoiner(", ", ElevatorStepCounter.class.getSimpleName() + "[", "]")
                .add("doorOpened=" + doorOpened)
                .add("slowMoves=" + slowMoves)
                .add("fastMoves=" + fastMoves)
                .toString();
    }
}
