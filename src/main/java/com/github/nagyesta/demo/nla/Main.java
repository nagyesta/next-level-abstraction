package com.github.nagyesta.demo.nla;

import com.github.nagyesta.demo.nla.model.world.Building;
import com.github.nagyesta.demo.nla.model.world.Elevator;
import com.github.nagyesta.demo.nla.model.world.ElevatorStatus;
import com.github.nagyesta.demo.nla.model.world.Floor;
import com.github.nagyesta.demo.nla.model.world.World;
import com.github.nagyesta.demo.nla.view.ConsolePresenter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.print("\u001b\u0063");
        final Map<String, Elevator> elevators = Stream.<Elevator>builder()
                .add(Elevator.builder().index(0).name("1")
                        .floor(0.0D).occupants(1)
                        .status(ElevatorStatus.DOOR_OPEN)
                        .build())
                .build().collect(Collectors.toMap(Elevator::getName, Function.identity()));
        Map<String, Floor> floors = Stream.<Floor>builder()
                .add(Floor.builder().index(0).name("0")
                        .occupants(0)
                        .build())
                .build().collect(Collectors.toMap(Floor::getName, Function.identity()));
        World world = World.builder()
                .building(Building.builder()
                        .elevatorsByName(elevators)
                        .floorsByName(floors)
                        .build())
                .build();
        new ConsolePresenter().renderFrame(System.out, world);
    }

}
