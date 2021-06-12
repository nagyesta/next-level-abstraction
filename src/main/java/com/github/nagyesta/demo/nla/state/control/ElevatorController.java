package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.FloorStats;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.List;
import java.util.Map;

public interface ElevatorController {

    /**
     * Programs the elevators of a building based on a fixed set of input.
     *
     * @param floorStats            The statistics we know about occupants of each floor.
     * @param elevatorsByIdleStatus The elevators by idle status.
     */
    void program(FloorStats floorStats, Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus);
}
