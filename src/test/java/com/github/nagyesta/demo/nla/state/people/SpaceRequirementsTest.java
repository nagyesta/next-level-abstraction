package com.github.nagyesta.demo.nla.state.people;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class SpaceRequirementsTest {

    @Test
    void testEquals() {
        //given

        //when
        EqualsVerifier.forClass(SpaceRequirements.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        //then
    }
}
