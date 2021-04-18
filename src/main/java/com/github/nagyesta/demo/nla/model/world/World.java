package com.github.nagyesta.demo.nla.model.world;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class World {

    private Building building;
    private int steps;
    private int trips;
}
