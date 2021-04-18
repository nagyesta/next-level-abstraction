package com.github.nagyesta.demo.nla.model.world;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Building {

    private Map<String, Floor> floorsByName;
    private Map<String, Elevator> elevatorsByName;
}
