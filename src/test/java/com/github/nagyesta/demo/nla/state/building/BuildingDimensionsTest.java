package com.github.nagyesta.demo.nla.state.building;

import com.github.nagyesta.demo.nla.state.people.Person;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static com.github.nagyesta.demo.nla.state.building.BuildingDimensions.MAX_ELEVATORS;
import static com.github.nagyesta.demo.nla.state.building.BuildingDimensions.MAX_ELEVATOR_OCCUPANTS;
import static com.github.nagyesta.demo.nla.state.building.BuildingDimensions.MAX_FLOORS;

class BuildingDimensionsTest {

    private static Stream<Arguments> constructorParameterProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(0, 0, 0, false))
                .add(Arguments.of(0, 1, 1, false))
                .add(Arguments.of(1, 0, 1, false))
                .add(Arguments.of(1, 1, 0, false))
                .add(Arguments.of(1, 1, 1, true))
                .add(Arguments.of(1, 2, 3, true))
                .add(Arguments.of(MAX_FLOORS, MAX_ELEVATORS, MAX_ELEVATOR_OCCUPANTS, true))
                .add(Arguments.of(MAX_FLOORS + 1, MAX_ELEVATORS, MAX_ELEVATOR_OCCUPANTS, false))
                .add(Arguments.of(MAX_FLOORS, MAX_ELEVATORS + 1, MAX_ELEVATOR_OCCUPANTS, false))
                .add(Arguments.of(MAX_FLOORS, MAX_ELEVATORS, MAX_ELEVATOR_OCCUPANTS + 1, false))
                .add(Arguments.of(MAX_FLOORS + 1, MAX_ELEVATORS + 1, MAX_ELEVATOR_OCCUPANTS + 1, false))
                .build();
    }

    private static Stream<Arguments> indexProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(-42, false))
                .add(Arguments.of(-1, false))
                .add(Arguments.of(0, true))
                .add(Arguments.of(1, true))
                .add(Arguments.of(3, true))
                .add(Arguments.of(5, true))
                .add(Arguments.of(6, false))
                .add(Arguments.of(42, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("constructorParameterProvider")
    void testConstructorShouldValidateInputWhenCalled(final int floors, final int elevators, final int capacity, final boolean valid) {
        //given

        //when
        Optional<BuildingDimensions> actual = Optional.empty();
        if (valid) {
            actual = Optional.of(new BuildingDimensions(floors, elevators, capacity));
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, () -> new BuildingDimensions(floors, elevators, capacity));
        }

        //then
        Assertions.assertEquals(valid, actual.isPresent());
        actual.ifPresent(a -> {
            Assertions.assertEquals(floors, a.getFloors());
            Assertions.assertEquals(floors - 1, a.getTopFloorIndex());
            Assertions.assertEquals(elevators, a.getElevators());
            Assertions.assertEquals(capacity, a.getElevatorMaxOccupants());
            Assertions.assertEquals(capacity * Person.AVERAGE_CAPACITY_TAKEN, a.getElevatorCapacity());
        });
    }

    @ParameterizedTest
    @MethodSource("indexProvider")
    void testRequireValidFloorIndexShouldCheckBoundariesInBothDirectionWhenCalled(final int index, final boolean expected) {
        //given
        final BuildingDimensions underTest = new BuildingDimensions(6, 6, 3);

        //when
        Integer actual = null;
        if (expected) {
            actual = underTest.requireValidFloorIndex(index);
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.requireValidFloorIndex(index));
        }

        //then
        if (expected) {
            Assertions.assertEquals(index, actual);
        }
    }

    @ParameterizedTest
    @MethodSource("indexProvider")
    void testRequireValidElevatorIndexShouldCheckBoundariesInBothDirectionWhenCalled(final int index, final boolean expected) {
        //given
        final BuildingDimensions underTest = new BuildingDimensions(6, 6, 3);

        //when
        Integer actual = null;
        if (expected) {
            actual = underTest.requireValidElevatorIndex(index);
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.requireValidElevatorIndex(index));
        }

        //then
        if (expected) {
            Assertions.assertEquals(index, actual);
        }
    }

    @Test
    void testEquals() {
        //given

        //when
        EqualsVerifier.forClass(BuildingDimensions.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        //then
    }
}
