package com.github.nagyesta.demo.nla.model.world;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Elevator implements NamedCoordinate, AvailableForPeople {

    private int index;
    private String name;
    private int occupants;
    private double floor;
    private ElevatorStatus status;

    public boolean hasDoorOnFloor(final int floorIndex) {
        return floorIndex == floor;
    }

    public boolean hasTopOnFloor(final int floorIndex) {
        return floorIndex == (floor + 0.5D);
    }

    public boolean hasBottomOnFloor(final int floorIndex) {
        return floorIndex == (floor - 0.5D);
    }

    public boolean isBelowFloor(final int floorIndex) {
        return floorIndex > (floor + 0.5D);
    }

    public boolean isMoving() {
        return status != ElevatorStatus.STOPPED && status != ElevatorStatus.DOOR_OPEN;
    }

    public boolean isDoorOpen() {
        return status == ElevatorStatus.DOOR_OPEN;
    }
}
