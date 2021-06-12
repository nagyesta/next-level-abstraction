package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.state.building.Place;
import com.github.nagyesta.demo.nla.state.building.TurnBased;

public interface Elevator extends Place, TurnBased, ProgrammableElevator {

    /**
     * The step counter providing metrics.
     *
     * @return counter
     */
    ElevatorStepCounter getStepCounter();

    /**
     * Removes a button press when the elevator arrives to a floor.
     */
    void removeButtonPressOnArrival();
}
