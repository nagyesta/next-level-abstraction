package com.github.nagyesta.demo.nla.state.people;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;

class PersonStepCounterTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 20, 42})
    void testRecordElevatorTravelShouldAddOneToTravelCounterEachTimeItIsCalled(final int count) {
        //given
        final PersonStepCounter underTest = new PersonStepCounter();

        //when
        IntStream.range(0, count)
                .forEach(i -> underTest.recordElevatorTravel());

        //then
        Assertions.assertEquals(count, underTest.getElevatorTravelTurns());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 20, 42})
    void testRecordElevatorWaitShouldAddOneToWaitCounterEachTimeItIsCalled(final int count) {
        //given
        final PersonStepCounter underTest = new PersonStepCounter();

        //when
        IntStream.range(0, count)
                .forEach(i -> underTest.recordElevatorWait());

        //then
        Assertions.assertEquals(count, underTest.getElevatorWaitTurns());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 20, 42})
    void testRecordTripCompleteShouldAddOneToTripCompleteCounterEachTimeItIsCalled(final int count) {
        //given
        final PersonStepCounter underTest = new PersonStepCounter();

        //when
        IntStream.range(0, count)
                .forEach(i -> underTest.recordTripComplete());

        //then
        Assertions.assertEquals(count, underTest.getTripsCompleted());
    }

    @Test
    void testEquals() {
        //given

        //when
        EqualsVerifier.forClass(PersonStepCounter.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        //then
    }
}
