package com.github.nagyesta.demo.nla.view;

import com.github.nagyesta.demo.nla.model.world.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ConsolePresenterIntegrationTest {

    private static Stream<Arguments> validDataProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(generateMinimalisticWorld(), "/console/output/expected-minimal-output.txt"))
                .add(Arguments.of(generateComplexWorld(), "/console/output/expected-complex-output.txt"))
                .build();
    }

    private static World generateMinimalisticWorld() {
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
        return World.builder()
                .building(Building.builder()
                        .elevatorsByName(elevators)
                        .floorsByName(floors)
                        .build())
                .build();
    }

    private static World generateComplexWorld() {
        final Map<String, Elevator> elevators = Stream.<Elevator>builder()
                .add(Elevator.builder().index(0).name("A")
                        .floor(0.0D).occupants(1)
                        .status(ElevatorStatus.DOOR_OPEN)
                        .build())
                .add(Elevator.builder().index(1).name("B")
                        .floor(4.5D).occupants(1)
                        .status(ElevatorStatus.MOVING_DOWN_FAST)
                        .build())
                .add(Elevator.builder().index(2).name("C")
                        .floor(0.5D).occupants(0)
                        .status(ElevatorStatus.MOVING_UP_SLOW)
                        .build())
                .add(Elevator.builder().index(3).name("D")
                        .floor(3.0D).occupants(0)
                        .status(ElevatorStatus.STOPPED)
                        .build())
                .add(Elevator.builder().index(4).name("E")
                        .floor(1.0D).occupants(0)
                        .status(ElevatorStatus.MOVING_UP_SLOW)
                        .build())
                .build().collect(Collectors.toMap(Elevator::getName, Function.identity()));
        Map<String, Floor> floors = Stream.<Floor>builder()
                .add(Floor.builder().index(0).name("G")
                        .occupants(249)
                        .build())
                .add(Floor.builder().index(1).name("1")
                        .occupants(0)
                        .build())
                .add(Floor.builder().index(2).name("2")
                        .occupants(109)
                        .build())
                .add(Floor.builder().index(3).name("3")
                        .occupants(99)
                        .build())
                .add(Floor.builder().index(4).name("4")
                        .occupants(49)
                        .build())
                .add(Floor.builder().index(5).name("5")
                        .occupants(19)
                        .build())
                .add(Floor.builder().index(6).name("6")
                        .occupants(9)
                        .build())
                .build().collect(Collectors.toMap(Floor::getName, Function.identity()));
        return World.builder()
                .building(Building.builder()
                        .elevatorsByName(elevators)
                        .floorsByName(floors)
                        .build())
                .build();
    }

    @ParameterizedTest
    @MethodSource("validDataProvider")
    void testRenderFrameShouldRenderTheExpectedFrameWhenCalledWithTheExample(World input, String expectedResource) throws IOException {
        //given
        final ConsolePresenter underTest = new ConsolePresenter();
        final String expected = IOUtils.resourceToString(expectedResource, StandardCharsets.UTF_8);
        final StringBuilder actual = new StringBuilder();

        //when
        underTest.renderFrame(actual, input);

        //then
        Assertions.assertArrayEquals(expected.split(System.lineSeparator()), actual.toString().split(System.lineSeparator()));
    }
}
