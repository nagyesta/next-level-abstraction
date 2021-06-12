package com.github.nagyesta.demo.nla.state.people;

import com.github.nagyesta.demo.nla.state.building.AbstractPlace;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class GenericPersonTest {


    @Test
    void testEquals() {
        //given
        final LocationHolder locationA = new LocationHolder();
        locationA.updateLocation(mock(GenericPerson.class), mock(AbstractPlace.class), new SpaceRequirements(1));
        final LocationHolder locationB = new LocationHolder();
        locationB.updateLocation(mock(GenericPerson.class), mock(AbstractPlace.class), new SpaceRequirements(2));

        //when
        EqualsVerifier.forClass(GenericPerson.class)
                .withPrefabValues(LocationHolder.class, locationA, locationB)
                .withOnlyTheseFields("id")
                .suppress(Warning.NULL_FIELDS)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        //then
    }

}
