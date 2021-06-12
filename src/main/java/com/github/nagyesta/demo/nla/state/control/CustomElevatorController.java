package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.FloorStats;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Consumer;

public class CustomElevatorController extends BaseElevatorController implements ElevatorController {

    public CustomElevatorController(final BuildingDimensions buildingDimensions) {
        super(buildingDimensions);
    }

    @Override
    public void program(final FloorStats floorStats,
                        final Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus) {
        elevatorsByIdleStatus.getOrDefault(true, Collections.emptyList())
                .forEach(doProgram(allStopsUp(), allStopsDown()));
    }

    protected Consumer<ProgrammableElevator> doProgram(final SortedSet<Integer> up, final SortedSet<Integer> down) {
        return elevator -> {
            if (elevator.isBelowFloor(getTopFloorIndex())) {
                elevator.overwriteProgramStoppingOn(up);
            } else {
                elevator.overwriteProgramStoppingOn(down);
            }
        };
    }

}
