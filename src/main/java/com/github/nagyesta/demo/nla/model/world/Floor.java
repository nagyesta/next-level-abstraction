package com.github.nagyesta.demo.nla.model.world;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Floor implements NamedCoordinate, AvailableForPeople {

    private int index;
    private String name;
    private int occupants;
    private int occupantsUp;
    private int occupantsDown;

}
