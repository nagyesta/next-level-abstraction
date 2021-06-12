package com.github.nagyesta.demo.nla.state.people;

import com.github.nagyesta.demo.nla.state.building.AbstractPlace;
import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.building.Floor;
import com.github.nagyesta.demo.nla.state.building.TurnBased;
import com.github.nagyesta.demo.nla.state.elevator.Elevator;

import java.util.Comparator;
import java.util.Set;

public interface Person extends Comparable<Person>, TurnBased {

    /**
     * Defines how we should sort a group of people in order to find their priority order.
     */
    Comparator<Person> WAIT_SINCE_COMPARATOR = Comparator.comparing(Person::getLastActionTime).thenComparing(Person::getId);
    /**
     * The average size/weight of a person.
     */
    int AVERAGE_CAPACITY_TAKEN = 100;

    /**
     * The unique id of this person.
     *
     * @return id
     */
    long getId();

    /**
     * The turn index when this person last performed an action.
     *
     * @return turn index
     */
    int getLastActionTime();

    /**
     * True if this person needs an elevator right know (wants to go to a different floor).
     *
     * @return elevator needed
     */
    boolean needsElevator();

    /**
     * The floor index of the next destination where this person wants to go.
     *
     * @return floor index
     */
    int nextDestination();

    /**
     * THe floor index of the current location where this person is.
     *
     * @return floor index
     */
    double onFloor();

    /**
     * The amount of available size/weight capacity this person needs in order to enter somewhere (personal preference).
     *
     * @return size
     */
    int getSpaceNeeded();

    /**
     * The amount of size/weight capacity used by this person when entering somewhere (actual use).
     *
     * @return size
     */
    int getSpaceTaken();

    /**
     * THe step counter holding stats about the actions of this person.
     *
     * @return counter
     */
    PersonStepCounter getStepCounter();

    /**
     * The attitude of this person based on next destination.
     *
     * @return attitude
     */
    Attitude attitude();

    /**
     * Called when this person enters a floor, letting people to leave.
     *
     * @param floor the floor where we need to enter
     */
    void arriveTo(Floor floor);

    /**
     * Called when the elevator arrives to the floor, letting people to get in..
     *
     * @param elevator The elevator
     */
    void elevatorArrived(Elevator elevator);

    /**
     * The button this person presses when entering the elevator.
     *
     * @return floor index, not-null
     */
    Set<Integer> pressButton();

    /**
     * Updates the location of this person (a technicality to allow updates).
     *
     * @param place The new location
     */
    void updateLocation(AbstractPlace place);

    /**
     * Returns true if the person completed the steps assigned.
     * @return steps empty
     */
    boolean finished();
}
