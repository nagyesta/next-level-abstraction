package com.github.nagyesta.demo.nla.state.elevator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ElevatorStatusTest {

    private static Stream<Arguments> travelDistanceProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(ElevatorStatus.DOOR_OPEN, 0.0D))
                .add(Arguments.of(ElevatorStatus.STOPPED, 0.0D))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_FAST, -1.0D))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_FAST, 1.0D))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_SLOW, -0.5D))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_SLOW, 0.5D))
                .build();
    }

    private static Stream<Arguments> movingSlowProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(ElevatorStatus.DOOR_OPEN, false))
                .add(Arguments.of(ElevatorStatus.STOPPED, false))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_FAST, false))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_FAST, false))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_SLOW, true))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_SLOW, true))
                .build();
    }

    private static Stream<Arguments> movingFastProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(ElevatorStatus.DOOR_OPEN, false))
                .add(Arguments.of(ElevatorStatus.STOPPED, false))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_FAST, true))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_FAST, true))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_SLOW, false))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_SLOW, false))
                .build();
    }

    private static Stream<Arguments> movingProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(ElevatorStatus.DOOR_OPEN, false))
                .add(Arguments.of(ElevatorStatus.STOPPED, false))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_FAST, true))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_FAST, true))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_SLOW, true))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_SLOW, true))
                .build();
    }

    private static Stream<Arguments> doorOpenProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(ElevatorStatus.DOOR_OPEN, true))
                .add(Arguments.of(ElevatorStatus.STOPPED, false))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_FAST, false))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_FAST, false))
                .add(Arguments.of(ElevatorStatus.MOVING_DOWN_SLOW, false))
                .add(Arguments.of(ElevatorStatus.MOVING_UP_SLOW, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("travelDistanceProvider")
    void testGetTravelPerTurnShouldReturnTheExpectedValue(final ElevatorStatus underTest, final double expected) {
        //given

        //when
        final double actual = underTest.getTravelPerTurn();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("doorOpenProvider")
    void testIsDoorOpenShouldReturnTheExpectedValue(final ElevatorStatus underTest, final boolean expected) {
        //given

        //when
        final boolean actual = underTest.isDoorOpen();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("movingProvider")
    void testIsMovingShouldReturnTheExpectedValue(final ElevatorStatus underTest, final boolean expected) {
        //given

        //when
        final boolean actual = underTest.isMoving();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("movingSlowProvider")
    void testIsMovingSlowShouldReturnTheExpectedValue(final ElevatorStatus underTest, final boolean expected) {
        //given

        //when
        final boolean actual = underTest.isMovingSlow();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("movingFastProvider")
    void testIsMovingFastShouldReturnTheExpectedValue(final ElevatorStatus underTest, final boolean expected) {
        //given

        //when
        final boolean actual = underTest.isMovingFast();

        //then
        Assertions.assertEquals(expected, actual);
    }
}
