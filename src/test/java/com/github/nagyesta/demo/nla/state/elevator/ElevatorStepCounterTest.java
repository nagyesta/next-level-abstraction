package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.state.program.Step;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

class ElevatorStepCounterTest {

    private static final Step<ElevatorStatus> DOWN_FAST_STEP = new Step<>(ElevatorStatus.MOVING_DOWN_FAST);
    private static final Step<ElevatorStatus> UP_FAST_STEP = new Step<>(ElevatorStatus.MOVING_UP_FAST);
    private static final Step<ElevatorStatus> DOWN_SLOW_STEP = new Step<>(ElevatorStatus.MOVING_DOWN_SLOW);
    private static final Step<ElevatorStatus> UP_SLOW_STEP = new Step<>(ElevatorStatus.MOVING_UP_SLOW);
    private static final Step<ElevatorStatus> STOPPED_STEP = new Step<>(ElevatorStatus.STOPPED);

    private static Stream<Arguments> stepProvider() {
        return IntStream.of(0, 1, 2, 3, 4, 5, 20, 42)
                .boxed().flatMap(i -> Stream.<Arguments>builder()
                        .add(Arguments.of(i, DOWN_FAST_STEP, 0, i))
                        .add(Arguments.of(i, DOWN_SLOW_STEP, i, 0))
                        .add(Arguments.of(i, UP_FAST_STEP, 0, i))
                        .add(Arguments.of(i, UP_SLOW_STEP, i, 0))
                        .add(Arguments.of(i, STOPPED_STEP, 0, 0))
                        .build());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 20, 42})
    void testRecordDoorOpenShouldAddOneToDoorOpenCounterEachTimeItIsCalled(final int count) {
        //given
        final ElevatorStepCounter underTest = new ElevatorStepCounter();

        //when
        IntStream.range(0, count)
                .forEach(i -> underTest.recordDoorOpen());

        //then
        Assertions.assertEquals(count, underTest.getDoorOpened());
    }

    @ParameterizedTest
    @MethodSource("stepProvider")
    void testRecordMoveShouldAddOneToTheRightCounterEachTimeItIsCalledWithAValidStep(
            final int count, final Step<ElevatorStatus> step, final int expectedSlow, final int expectedFast) {
        //given
        final ElevatorStepCounter underTest = new ElevatorStepCounter();

        //when
        IntStream.range(0, count)
                .forEach(i -> underTest.recordMove(step));

        //then
        Assertions.assertEquals(expectedFast, underTest.getFastMoves());
        Assertions.assertEquals(expectedSlow, underTest.getSlowMoves());
    }

    @Test
    void testEquals() {
        //given

        //when
        EqualsVerifier.forClass(ElevatorStepCounter.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        //then
    }
}
