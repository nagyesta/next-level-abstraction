package com.github.nagyesta.demo.nla.state.people;

import com.github.nagyesta.demo.nla.state.program.Step;

public class StepEvaluator {

    public boolean destinationReached(final Person person, final Step<Action> nextStep) {
        return shouldMove(nextStep) && person.nextDestination() == person.onFloor();
    }

    public boolean wantsToEnter(final Step<Action> nextStep) {
        return nextStep != null && nextStep.getAction() == Action.ENTER;
    }

    public boolean wantsToLeave(final Step<Action> nextStep) {
        return nextStep != null && nextStep.getAction() == Action.LEAVE;
    }

    public boolean shouldWait(final Step<Action> nextStep) {
        return nextStep != null && nextStep.getAction() == Action.WAIT;
    }

    public boolean shouldMove(final Step<Action> nextStep) {
        return nextStep != null && nextStep.getAction() == Action.MOVE;
    }
}
