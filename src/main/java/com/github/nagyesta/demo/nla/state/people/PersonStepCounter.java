package com.github.nagyesta.demo.nla.state.people;

import com.github.nagyesta.demo.nla.annotation.ExcludeIdeGeneratedToStringFromJacoco;

import java.util.Objects;
import java.util.StringJoiner;

public class PersonStepCounter {

    private int elevatorTravelTurns = 0;
    private int elevatorWaitTurns = 0;
    private int tripsCompleted = 0;

    public int getElevatorTravelTurns() {
        return elevatorTravelTurns;
    }

    public int getElevatorWaitTurns() {
        return elevatorWaitTurns;
    }

    public int getTripsCompleted() {
        return tripsCompleted;
    }

    public void recordElevatorTravel() {
        elevatorTravelTurns++;
    }

    public void recordElevatorWait() {
        elevatorWaitTurns++;
    }

    public void recordTripComplete() {
        tripsCompleted++;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonStepCounter)) {
            return false;
        }
        final PersonStepCounter that = (PersonStepCounter) o;
        return elevatorTravelTurns == that.elevatorTravelTurns
                && elevatorWaitTurns == that.elevatorWaitTurns
                && tripsCompleted == that.tripsCompleted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elevatorTravelTurns, elevatorWaitTurns, tripsCompleted);
    }

    @Override
    @ExcludeIdeGeneratedToStringFromJacoco
    public String toString() {
        return new StringJoiner(", ", PersonStepCounter.class.getSimpleName() + "[", "]")
                .add("travel=" + elevatorTravelTurns)
                .add("wait=" + elevatorWaitTurns)
                .add("trips=" + tripsCompleted)
                .toString();
    }
}
