package com.github.nagyesta.demo.nla.state.program;

public interface BehaviorBuilder {

    /**
     * Wait a few turns, then enter the building on the ground floor.
     *
     * @param turns Wait time.
     * @return this
     */
    Move enterAfter(int turns);
}
