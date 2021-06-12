package com.github.nagyesta.demo.nla.state.people.special;

import com.github.nagyesta.demo.nla.state.elevator.Elevator;
import com.github.nagyesta.demo.nla.state.people.Action;
import com.github.nagyesta.demo.nla.state.people.GenericPerson;
import com.github.nagyesta.demo.nla.state.people.SpaceRequirements;
import com.github.nagyesta.demo.nla.state.program.Step;

import java.util.Queue;

public class LoneWolf extends GenericPerson {

    public LoneWolf(final Queue<Step<Action>> steps,
                    final SpaceRequirements spaceRequirements) {
        super(steps, spaceRequirements);
    }

    @Override
    protected boolean wantsToEnter(final Elevator elevator) {
        return super.wantsToEnter(elevator) && elevator.getFilledCapacity() == 0;
    }

}
