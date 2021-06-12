package com.github.nagyesta.demo.nla.state.building;

import com.github.nagyesta.demo.nla.state.people.Person;

import java.util.Set;

public interface Place {

    /**
     * Returns the building encapsulating this place.
     *
     * @return building
     */
    Building getBuilding();

    /**
     * The total size/weight occupants can have total.
     *
     * @return total
     */
    int getTotalCapacity();

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
     * The floor index where this place is (constant for floors), the ground floor is 0.
     *
     * @return index
     */
    double getFloorIndex();

    /**
     * Lets a person leave this place.
     *
     * @param person The person leaving.
     */
    void remove(Person person);

    /**
     * Lets a person join this place.
     *
     * @param person The person entering.
     */
    void accept(Person person);

    /**
     * Everyone who is currently inside this place.
     *
     * @return occupants
     */
    Set<Person> population();

    /**
     * Moves a person to s new place.
     *
     * @param to     The destination
     * @param person The person
     */
    void movePeople(AbstractPlace to, Person person);
}
