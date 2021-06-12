package com.github.nagyesta.demo.nla.state.program;

public interface Move {

    /**
     * Wait a few turns, then move to the given floor.
     *
     * @param turns Wait time.
     * @param floor the floor where the person will want to go
     * @return this
     */
    Move waitAndMoveTo(int turns, int floor);

    /**
     * Generates the necessary number of meetings on other floors for the employee before returning to their starting floor.
     *
     * @param currentFloor The floor where the person is at the moment.
     * @param meetingCount The number of meetings the person should have.
     * @return this
     */
    Move addMeetings(int currentFloor, int meetingCount);

    /**
     * Wait a few turns, then leave the ground floor. Must be called after the person went to the ground floor.
     *
     * @param turns Wait time.
     * @return this
     */
    Build waitAndLeave(int turns);
}
