package com.github.nagyesta.demo.nla.state.program;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.people.Action;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class PersonalBehaviorBuilder implements BehaviorBuilder, Move, Build {

    /**
     * The length of an average workday.
     */
    public static final int WORKDAY = 180;
    /**
     * The time when work starts in the building.
     */
    public static final int WORKSTART = 70;

    private final BuildingDimensions buildingDimensions;
    private LinkedList<Step<Action>> queue = new LinkedList<>();

    private PersonalBehaviorBuilder(final BuildingDimensions buildingDimensions) {
        this.buildingDimensions = Objects.requireNonNull(buildingDimensions);
    }

    public static BehaviorBuilder builder(final BuildingDimensions buildingDimensions) {
        return new PersonalBehaviorBuilder(buildingDimensions);
    }

    @Override
    public Move enterAfter(final int turns) {
        this.wait(turns);
        this.queue.add(new Step<>(Action.ENTER));
        return this;
    }

    @Override
    public Move waitAndMoveTo(final int turns, final int floor) {
        this.wait(turns);
        this.queue.add(new FloorAwareStep<>(Action.MOVE, buildingDimensions.requireValidFloorIndex(floor)));
        return this;
    }

    @Override
    public Move addMeetings(final int workFloorIndex, final int meetingCount) {
        if (meetingCount < 1) {
            wait(WORKDAY);
            return this;
        }
        int currentFloor = workFloorIndex;
        final int duration = WORKDAY / meetingCount;
        for (int i = 0; i < meetingCount; i++) {
            if (currentFloor <= workFloorIndex && buildingDimensions.getTopFloorIndex() > currentFloor) {
                currentFloor++;
            } else if (currentFloor > 0) {
                currentFloor--;
            }
            this.waitAndMoveTo(duration, currentFloor);
        }
        return this.waitAndMoveTo(duration, workFloorIndex);
    }

    @Override
    public Build waitAndLeave(final int turns) {
        this.wait(turns);
        this.queue.add(new Step<>(Action.LEAVE));
        return this;
    }

    @Override
    public Queue<Step<Action>> build() {
        final Queue<Step<Action>> result = this.queue;
        this.queue = new LinkedList<>();
        return result;
    }

    private void wait(final int turns) {
        if (turns < 1) {
            throw new IllegalArgumentException("We must wait at least 1 turn.");
        }
        for (int i = 0; i < turns; i++) {
            this.queue.add(new Step<>(Action.WAIT));
        }
    }

}
