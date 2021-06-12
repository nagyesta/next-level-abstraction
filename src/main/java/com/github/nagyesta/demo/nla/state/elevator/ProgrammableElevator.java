package com.github.nagyesta.demo.nla.state.elevator;

import com.github.nagyesta.demo.nla.model.world.NamedCoordinate;
import com.github.nagyesta.demo.nla.state.building.Attitude;

import java.util.Set;
import java.util.SortedSet;

public interface ProgrammableElevator extends NamedCoordinate {

    /**
     * The size/weight sum of the occupants.
     *
     * @return filled
     */
    int getFilledCapacity();

    /**
     * The size/weight still available for additional people.
     *
     * @return free
     */
    int getFreeCapacity();

    /**
     * True if there is no space for additional people.
     *
     * @return full
     */
    boolean isFull();

    /**
     * Returns the distance from the given floor. Using absolute values.
     *
     * @param floorIndex the index of the floor we want to know how far it is.
     * @return distance
     */
    default double distanceFrom(final int floorIndex) {
        return Math.abs(getFloorIndex() - floorIndex);
    }

    /**
     * The floor index where this place is (constant for floors), the ground floor is 0.
     *
     * @return index
     */
    double getFloorIndex();

    /**
     * The current status of the elevator.
     *
     * @return status
     */
    ElevatorStatus getStatus();

    /**
     * True if the current floor index, attitude and status are suitable for stopping at the given floor.
     *
     * @param floorIndex The floor where we want to stop
     * @return can stop
     */
    boolean canSafelyStopOnFloor(int floorIndex);

    /**
     * True if the elevator door is on the given floor
     *
     * @param floorIndex The floor
     * @return is on floor
     */
    boolean hasDoorOnFloor(int floorIndex);

    /**
     * True if the elevator door is above the given floor
     *
     * @param floorIndex The floor
     * @return is above floor
     */
    boolean isAboveFloor(int floorIndex);

    /**
     * True if the elevator door is below the given floor
     *
     * @param floorIndex The floor
     * @return is below floor
     */
    boolean isBelowFloor(int floorIndex);

    /**
     * Returns the attitude where the elevator is heading.
     *
     * @return attitude
     */
    Attitude getAttitude();

    /**
     * Returns the buttons of the elevator which are currently pressed.
     *
     * @return pressed
     */
    SortedSet<Integer> getButtonsPressedInCar();

    /**
     * Returns the floor indexes the elevator will go to based on the last program.
     *
     * @return pressed
     */
    Set<Integer> getPlannedStops();

    /**
     * True if the elevator has nothing to do.
     *
     * @return is program complete
     */
    boolean isIdle();

    /**
     * Returns true is the door was closed in the very last step.
     *
     * @return stopped in last turn
     */
    boolean justClosedDoor();

    /**
     * Returns true if the elevator can go fast on the provided distance.
     *
     * @param fromFloor Floor where the elevator would start from
     * @param toFloor   Destination floor
     * @return fast
     */
    boolean canGoFast(double fromFloor, int toFloor);

    /**
     * Overwrites the program of the elevator.
     *
     * @param floors Where the elevator should stop.
     */
    void overwriteProgramStoppingOn(SortedSet<Integer> floors);
}
