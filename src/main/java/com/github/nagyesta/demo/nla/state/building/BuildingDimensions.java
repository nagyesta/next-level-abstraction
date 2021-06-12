package com.github.nagyesta.demo.nla.state.building;

import com.github.nagyesta.demo.nla.state.people.Person;

import java.util.Objects;

public class BuildingDimensions {
    public static final int MAX_FLOORS = 12;
    public static final int MAX_ELEVATORS = 10;
    public static final int MAX_ELEVATOR_OCCUPANTS = 8;
    private final int elevators;
    private final int floors;
    private final int elevatorMaxOccupants;

    public BuildingDimensions(final int floors, final int elevators, final int elevatorMaxOccupants) {
        if (floors <= 0 || floors > MAX_FLOORS) {
            throw new IllegalArgumentException("You need at least 1 floors and you cannot have more than "
                    + MAX_FLOORS + ".");
        }
        if (elevators <= 0 || elevators > MAX_ELEVATORS) {
            throw new IllegalArgumentException("You need at least 1 elevator and you cannot have more than "
                    + MAX_ELEVATORS + ".");
        }
        if (elevatorMaxOccupants <= 0 || elevatorMaxOccupants > MAX_ELEVATOR_OCCUPANTS) {
            throw new IllegalArgumentException("An elevator must have a capacity of at least 1 and no more than "
                    + MAX_ELEVATOR_OCCUPANTS + ".");
        }
        this.elevators = elevators;
        this.elevatorMaxOccupants = elevatorMaxOccupants;
        this.floors = floors;
    }

    public int getElevators() {
        return elevators;
    }

    public int getFloors() {
        return floors;
    }

    public int getTopFloorIndex() {
        return floors - 1;
    }

    public int getElevatorMaxOccupants() {
        return elevatorMaxOccupants;
    }

    public int getElevatorCapacity() {
        return elevatorMaxOccupants * Person.AVERAGE_CAPACITY_TAKEN;
    }

    public int requireValidFloorIndex(final int floor) {
        if (floor >= floors || floor < 0) {
            throw new IllegalArgumentException("Invalid floor index: " + floor);
        }
        return floor;
    }

    public int requireValidElevatorIndex(final int elevator) {
        if (elevator >= elevators || elevator < 0) {
            throw new IllegalArgumentException("Invalid elevator index: " + elevator);
        }
        return elevator;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BuildingDimensions)) {
            return false;
        }
        final BuildingDimensions that = (BuildingDimensions) o;
        return elevators == that.elevators && floors == that.floors && elevatorMaxOccupants == that.elevatorMaxOccupants;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elevators, floors, elevatorMaxOccupants);
    }
}
