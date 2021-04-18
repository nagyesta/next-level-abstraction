package com.github.nagyesta.demo.nla.state;

import com.github.nagyesta.demo.nla.model.world.ElevatorStatus;
import com.github.nagyesta.demo.nla.state.people.Person;
import lombok.Data;

import java.util.Collection;
import java.util.TreeSet;

@Data
public class ElevatorCar {
    private int index;
    private double position;
    private ElevatorMovement movement;
    private Collection<Person> occupant;
    private TreeSet<Integer> buttonsPressed;
    private ElevatorStatus status;

    public void takePerson(Person person) {
        buttonsPressed.add(person.nextDestination());
    }
}
