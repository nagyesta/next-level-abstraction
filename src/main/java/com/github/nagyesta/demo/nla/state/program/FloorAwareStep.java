package com.github.nagyesta.demo.nla.state.program;

import java.util.Optional;

public class FloorAwareStep<T extends Enum<T>> extends Step<T> {

    private final int floorIndex;

    public FloorAwareStep(final T action, final int floorIndex) {
        super(action);
        this.floorIndex = floorIndex;
    }

    @Override
    public Optional<Integer> getDestination() {
        return Optional.of(floorIndex);
    }
}
