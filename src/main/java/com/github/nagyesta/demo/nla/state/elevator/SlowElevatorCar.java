package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.state.building.Building;

public class SlowElevatorCar extends ElevatorCar {

    public SlowElevatorCar(final Building building, final int index, final String name, final int totalCapacity) {
        super(building, index, name, totalCapacity);
    }

    @Override
    public boolean canGoFast(final double fromFloor, final int toFloor) {
        return false;
    }
}
