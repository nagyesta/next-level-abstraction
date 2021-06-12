package com.github.nagyesta.demo.nla.state.program;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class StepTest {

    @Test
    void testEquals() {
        //given

        //when
        EqualsVerifier.forClass(Step.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        //then
    }
}
