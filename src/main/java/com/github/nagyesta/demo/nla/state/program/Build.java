package com.github.nagyesta.demo.nla.state.program;

import com.github.nagyesta.demo.nla.state.people.Action;

import java.util.Queue;

public interface Build {

    /**
     * Completes the chain and resets the builder.
     *
     * @return behavior
     */
    Queue<Step<Action>> build();
}
