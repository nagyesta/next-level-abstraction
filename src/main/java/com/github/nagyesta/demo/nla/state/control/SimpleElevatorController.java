package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.FloorStats;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class SimpleElevatorController extends BaseElevatorController {

    public SimpleElevatorController(final BuildingDimensions buildingDimensions) {
        super(buildingDimensions);
    }

    @Override
    public void program(final FloorStats floorStats,
                        final Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus) {
        final TreeSet<Integer> floorsWaiting = new TreeSet<>();
        floorsWaiting.addAll(floorStats.waitingToGoUp());
        floorsWaiting.addAll(floorStats.waitingToGoDown());
        doProgram(floorsWaiting, elevatorsByIdleStatus);
    }

    protected abstract void doProgram(SortedSet<Integer> floorsWaiting,
                                      Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus);
}
