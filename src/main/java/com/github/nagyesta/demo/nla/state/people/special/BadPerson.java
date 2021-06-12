package com.github.nagyesta.demo.nla.state.people.special;

import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.people.Action;
import com.github.nagyesta.demo.nla.state.people.GenericPerson;
import com.github.nagyesta.demo.nla.state.people.SpaceRequirements;
import com.github.nagyesta.demo.nla.state.program.Step;

import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BadPerson extends GenericPerson {

    public BadPerson(final Queue<Step<Action>> steps,
                     final SpaceRequirements spaceRequirements) {
        super(steps, spaceRequirements);
    }

    @Override
    public Set<Integer> pressButton() {
        final int lower;
        final int upper;
        if (attitude() == Attitude.NEGATIVE) {
            lower = nextDestination();
            upper = Math.max((int) onFloor() - 1, nextDestination());
        } else {
            lower = Math.min((int) onFloor() + 1, nextDestination());
            upper = nextDestination();
        }
        return IntStream.rangeClosed(lower, upper)
                .boxed()
                .collect(Collectors.toSet());
    }
}
