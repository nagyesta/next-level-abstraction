package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.FloorStats;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public abstract class DirectionAwareElevatorController extends BaseElevatorController {

    public DirectionAwareElevatorController(final BuildingDimensions buildingDimensions) {
        super(buildingDimensions);
    }

    @Override
    public void program(final FloorStats floorStats,
                        final Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus) {
        doProgram(floorStats.waitingToGoUp(), floorStats.waitingToGoDown(), elevatorsByIdleStatus);
    }

    protected abstract void doProgram(SortedSet<Integer> waitingToGoUp,
                                      SortedSet<Integer> waitingToGoDown,
                                      Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus);
}
