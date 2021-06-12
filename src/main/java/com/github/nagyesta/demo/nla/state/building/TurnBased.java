package com.github.nagyesta.demo.nla.state.building;

public interface TurnBased {

    /**
     * The method symbolizing the passing of time.
     *
     * @param turnIndex The index of the next turn
     */
    void nextTurn(int turnIndex);

}
