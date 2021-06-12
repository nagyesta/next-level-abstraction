package com.github.nagyesta.demo.nla.state.control;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.FloorStats;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class InfiniteMonkeysWithShortcutsElevatorController extends InfiniteMonkeysElevatorController {

    public InfiniteMonkeysWithShortcutsElevatorController(final BuildingDimensions buildingDimensions) {
        super(buildingDimensions);
    }

    @Override
    public void program(final FloorStats floorStats,
                        final Map<Boolean, List<ProgrammableElevator>> elevatorsByIdleStatus) {
        elevatorsByIdleStatus.getOrDefault(true, Collections.emptyList()).stream()
                .filter(group(0))
                .forEach(doProgram(allStopsUp(), allStopsDown()));
        elevatorsByIdleStatus.getOrDefault(true, Collections.emptyList()).stream()
                .filter(group(1))
                .forEach(doProgram(allStopsUp(), toGroundFast()));
        elevatorsByIdleStatus.getOrDefault(true, Collections.emptyList()).stream()
                .filter(group(2))
                .forEach(doProgram(toTopFast(), allStopsDown()));
    }

    private Predicate<ProgrammableElevator> group(final int index) {
        return elevator -> elevator.getIndex() % 3 == index;
    }

}
