package com.github.nagyesta.demo.nla.state.building;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Floor extends AbstractPlace implements TurnBased {

    protected Floor(final Building building, final int index, final String name) {
        super(building, index, name, index, Integer.MAX_VALUE);
    }

    @Override
    public void nextTurn(final int turnIndex) {
        this.population().forEach(p -> p.nextTurn(turnIndex));
    }

    public Map<Attitude, Integer> stats() {
        return Arrays.stream(Attitude.values())
                .collect(Collectors.toMap(Function.identity(), this::countPeople));
    }

    protected int countPeople(final Attitude attitude) {
        return (int) this.population().stream().filter(p -> p.attitude() == attitude).count();
    }
}
