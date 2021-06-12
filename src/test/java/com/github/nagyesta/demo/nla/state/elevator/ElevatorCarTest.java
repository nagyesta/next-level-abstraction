package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.building.Building;
import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.StockRoom;
import com.github.nagyesta.demo.nla.state.control.ElevatorController;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

class ElevatorCarTest {

    private static Stream<Arguments> safeStopProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(0.0D, 0, ElevatorStatus.STOPPED, true))
                .add(Arguments.of(1.0D, 1, ElevatorStatus.DOOR_OPEN, true))
                .add(Arguments.of(0.5D, 1, ElevatorStatus.MOVING_UP_SLOW, true))
                .add(Arguments.of(1.0D, 1, ElevatorStatus.MOVING_UP_SLOW, false))
                .add(Arguments.of(0.0D, 4, ElevatorStatus.STOPPED, true))
                .add(Arguments.of(0.5D, 1, ElevatorStatus.MOVING_UP_FAST, false))
                .add(Arguments.of(1.5D, 0, ElevatorStatus.MOVING_DOWN_SLOW, true))
                .add(Arguments.of(1.5D, 0, ElevatorStatus.MOVING_DOWN_FAST, true))
                .add(Arguments.of(1.5D, 0, ElevatorStatus.MOVING_UP_FAST, false))
                .build();
    }

    private static Stream<Arguments> locationProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(0.0D, 0))
                .add(Arguments.of(1.0D, 1))
                .add(Arguments.of(0.5D, 1))
                .add(Arguments.of(1.0D, 1))
                .add(Arguments.of(0.0D, 4))
                .add(Arguments.of(0.5D, 1))
                .add(Arguments.of(1.5D, 0))
                .add(Arguments.of(1.5D, 0))
                .add(Arguments.of(1.5D, 0))
                .build();
    }

    @ParameterizedTest
    @MethodSource("safeStopProvider")
    void testCanSafelyStopOnFloorShouldReturnFalseWhenCalledWhileMovingFastAndFloorIsTooClose(
            final double elevatorFloor, final int floor, final ElevatorStatus status, final boolean expected) {
        //given
        final Building building = new BuildingStub(5);
        final ElevatorCar underTest = new ElevatorCar(building, 0, "A", 1);
        underTest.setFloorIndex(elevatorFloor);
        underTest.setStatus(status);
        underTest.setAttitude(Attitude.forMovement(elevatorFloor, elevatorFloor + status.getTravelPerTurn()));

        //when
        final boolean actual = underTest.canSafelyStopOnFloor(floor);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("locationProvider")
    void testHasDoorOnFloorShouldReturnTrueOnlyWhenTheNumbersMatch(final double elevatorFloor, final int floor) {
        //given
        final Building building = new BuildingStub(5);
        final ElevatorCar underTest = new ElevatorCar(building, 0, "A", 1);
        underTest.setFloorIndex(elevatorFloor);

        //when
        final boolean actual = underTest.hasDoorOnFloor(floor);

        //then
        Assertions.assertEquals(elevatorFloor == floor, actual);
    }

    @ParameterizedTest
    @MethodSource("locationProvider")
    void testIsAboveFloorShouldReturnTrueOnlyWhenTheNumbersMatch(final double elevatorFloor, final int floor) {
        //given
        final Building building = new BuildingStub(5);
        final ElevatorCar underTest = new ElevatorCar(building, 0, "A", 1);
        underTest.setFloorIndex(elevatorFloor);

        //when
        final boolean actual = underTest.isAboveFloor(floor);

        //then
        Assertions.assertEquals(elevatorFloor > floor, actual);
    }

    @ParameterizedTest
    @MethodSource("locationProvider")
    void testIsBelowFloorShouldReturnTrueOnlyWhenTheNumbersMatch(final double elevatorFloor, final int floor) {
        //given
        final Building building = new BuildingStub(5);
        final ElevatorCar underTest = new ElevatorCar(building, 0, "A", 1);
        underTest.setFloorIndex(elevatorFloor);

        //when
        final boolean actual = underTest.isBelowFloor(floor);

        //then
        Assertions.assertEquals(elevatorFloor < floor, actual);
    }

    @Test
    void testEquals() {
        //given
        final Building buildingA = new BuildingStub(1);
        final Building buildingB = new BuildingStub(2);

        //when
        EqualsVerifier.forClass(ElevatorCar.class)
                .withPrefabValues(Building.class, buildingA, buildingB)
                .withOnlyTheseFields("building", "index")
                .suppress(Warning.NULL_FIELDS)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        //then
    }

    private static class BuildingStub extends Building {
        public BuildingStub(final int i) {
            super(buildingDimensions -> mock(ElevatorController.class),
                    building -> mock(StockRoom.class),
                    new BuildingDimensions(i, i, i));
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof BuildingStub)) {
                return false;
            }
            return this.getBuildingDimensions().equals(((BuildingStub) o).getBuildingDimensions());
        }
    }
}
