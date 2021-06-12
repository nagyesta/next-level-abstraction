package com.github.nagyesta.demo.nla.state.people;

import com.github.nagyesta.demo.nla.state.building.AbstractPlace;
import com.github.nagyesta.demo.nla.state.building.Building;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.nagyesta.demo.nla.state.building.Building.STOCKROOM_FLOOR_INDEX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocationHolderTest {

    @Test
    void testEquals() {
        //given
        final AbstractPlace placeA = new SimpleFloor(0);
        final AbstractPlace placeB = new SimpleFloor(1);

        //when
        EqualsVerifier.forClass(LocationHolder.class)
                .withPrefabValues(AbstractPlace.class, placeA, placeB)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        //then
    }

    @Test
    void testOnFloorShouldReturnTheStockRoomWhenLocationIsNull() {
        //given
        final LocationHolder underTest = new LocationHolder();

        //when
        final double actual = underTest.onFloor();

        //then
        Assertions.assertEquals(STOCKROOM_FLOOR_INDEX, actual);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testIsInElevatorShouldReturnTrueWhenLocationCanMove(final boolean expected) {
        //given
        final LocationHolder underTest = new LocationHolder();
        final AbstractPlace place = mock(AbstractPlace.class);
        when(place.canMove()).thenReturn(expected);
        underTest.updateLocation(mock(Person.class), place, new SpaceRequirements(5));

        //when
        final boolean actual = underTest.isInElevator();

        //then
        Assertions.assertEquals(expected, actual);
    }

    private static class SimpleFloor extends AbstractPlace {

        public SimpleFloor(final int index) {
            super(mock(Building.class), index, String.valueOf(index), index, Integer.MAX_VALUE);
        }

        @Override
        public void nextTurn(final int turnIndex) {
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof SimpleFloor)) {
                return false;
            }
            return this.getIndex() == ((SimpleFloor) o).getIndex();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
