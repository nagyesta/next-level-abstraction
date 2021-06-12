package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.state.building.Attitude;

public interface MutableElevator extends Elevator {

    /**
     * Sets the elevator status.
     *
     * @param elevatorStatus The new status
     */
    void setStatus(ElevatorStatus elevatorStatus);

    /**
     * Sets the elevator attitude.
     *
     * @param attitude The new attitude
     */
    void setAttitude(Attitude attitude);

    /**
     * The floor index where the elevator is
     *
     * @param floorIndex new location
     */
    void setFloorIndex(double floorIndex);
}
