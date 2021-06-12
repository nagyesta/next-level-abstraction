package com.github.nagyesta.demo.nla.state.building;

import com.github.nagyesta.demo.nla.state.control.ElevatorController;
import com.github.nagyesta.demo.nla.state.elevator.Elevator;
import com.github.nagyesta.demo.nla.state.people.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

class BuildingTest {

    private static final ElevatorController DUMMY_ELEVATOR_CONTROLLER = (floorStats, elevatorsByIdleStatus) -> {
    };

    private static Stream<Arguments> buildingProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(1, 1, 1))
                .add(Arguments.of(2, 3, 4))
                .add(Arguments.of(1, 2, 3))
                .add(Arguments.of(3, 1, 2))
                .build();
    }

    @ParameterizedTest
    @MethodSource("buildingProvider")
    public void testBuildingConstructorShouldGenerateFloorsAndElevatorsAsExpected(
            final int floors, final int elevators, final int elevatorCapacity) {
        //given
        final String[] elevatorNames = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        final String[] floorNames = {" G", " 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8"};
        final Function<Building, StockRoom> stockRoom = StockRoom.withPeople(Collections.emptySet());
        final BuildingDimensions dimensions = new BuildingDimensions(floors, elevators, elevatorCapacity);

        //when
        final Building underTest = new Building(d -> DUMMY_ELEVATOR_CONTROLLER, stockRoom, dimensions);

        //then
        for (int i = 0; i < elevators; i++) {
            final Elevator elevator = underTest.elevator(i);
            Assertions.assertNotNull(elevator);
            Assertions.assertEquals(i, elevator.getIndex());
            Assertions.assertEquals(elevatorNames[i], elevator.getName());
            Assertions.assertEquals(Person.AVERAGE_CAPACITY_TAKEN * elevatorCapacity, elevator.getFreeCapacity());
            Assertions.assertEquals(0.0D, elevator.getFloorIndex());
            Assertions.assertIterableEquals(Collections.emptySet(), elevator.getButtonsPressedInCar());
        }
        for (int i = 0; i < floors; i++) {
            final Floor floor = underTest.floor(i);
            Assertions.assertNotNull(floor);
            Assertions.assertEquals(i, floor.getIndex());
            Assertions.assertEquals(floorNames[i], floor.getName());
            Assertions.assertEquals(0, floor.getFilledCapacity());
            Assertions.assertIterableEquals(Collections.emptySet(), floor.population());
            Assertions.assertEquals(i, floor.getFloorIndex());
        }
    }
}
