package com.github.nagyesta.demo.nla.model.world;

import com.github.nagyesta.demo.nla.state.building.Attitude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
public class World {

    private Building building;
    private int steps;
    private int trips;

    public static World fromBuilding(final com.github.nagyesta.demo.nla.state.building.Building building) {
        final com.github.nagyesta.demo.nla.state.building.Building buildingState = Objects.requireNonNull(building);
        final Map<String, Elevator> elevatorsByName = convertElevators(buildingState);
        final Map<String, Floor> floorsByName = convertFloors(buildingState);

        return builder()
                .building(Building.builder()
                        .elevatorsByName(elevatorsByName)
                        .floorsByName(floorsByName)
                        .build())
                .steps(buildingState.getLastTurn())
                .trips(building.totalTrips())
                .build();
    }

    private static Map<String, Floor> convertFloors(final com.github.nagyesta.demo.nla.state.building.Building buildingState) {
        return buildingState.floors().stream()
                .map(floor -> Floor.builder()
                        .name(floor.getName())
                        .index(floor.getIndex())
                        .occupants((int) floor.population().stream().filter(p -> p.attitude() == Attitude.CONSTANT).count())
                        .occupantsUp((int) floor.population().stream().filter(p -> p.attitude() == Attitude.POSITIVE).count())
                        .occupantsDown((int) floor.population().stream().filter(p -> p.attitude() == Attitude.NEGATIVE).count())
                        .build())
                .collect(Collectors.toMap(Floor::getName, Function.identity()));
    }

    private static Map<String, Elevator> convertElevators(final com.github.nagyesta.demo.nla.state.building.Building buildingState) {
        return buildingState.elevators().stream()
                .map(elevator -> Elevator.builder()
                        .status(ElevatorStatus.valueOf(elevator.getStatus().name()))
                        .floor(elevator.getFloorIndex())
                        .name(elevator.getName())
                        .index(elevator.getIndex())
                        .occupants(elevator.population().size())
                        .build())
                .collect(Collectors.toMap(Elevator::getName, Function.identity()));
    }
}
