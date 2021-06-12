package com.github.nagyesta.demo.nla.state.program;

import com.github.nagyesta.demo.nla.annotation.ExcludeIdeGeneratedToStringFromJacoco;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class Step<T extends Enum<T>> {

    private final T action;

    public Step(final T action) {
        this.action = Objects.requireNonNull(action);
    }

    public T getAction() {
        return action;
    }

    public Optional<Integer> getDestination() {
        return Optional.empty();
    }

    @Override
    @ExcludeIdeGeneratedToStringFromJacoco
    public String toString() {
        return new StringJoiner(", ", Step.class.getSimpleName() + "[", "]")
                .add(action.name())
                .add(getDestination().map(String::valueOf).orElse("-"))
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Step)) {
            return false;
        }
        final Step<?> step = (Step<?>) o;
        return getDestination().equals(step.getDestination()) && action == step.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, getDestination());
    }
}
