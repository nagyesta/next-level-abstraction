package com.github.nagyesta.demo.nla.state.building;

import com.github.nagyesta.demo.nla.annotation.ExcludeIdeGeneratedToStringFromJacoco;
import com.github.nagyesta.demo.nla.model.world.NamedCoordinate;
import com.github.nagyesta.demo.nla.state.people.Person;

import java.util.*;

public abstract class AbstractPlace implements NamedCoordinate, TurnBased, Place {

    private final Building building;
    private final Set<Person> population = new HashSet<>();
    private final int index;
    private final String name;
    private final int totalCapacity;
    private double floorIndex;
    private Attitude attitude = Attitude.CONSTANT;

    protected AbstractPlace(final Building building, final int index, final String name, final double floorIndex, final int totalCapacity) {
        this.building = Objects.requireNonNull(building);
        this.index = index;
        this.name = Objects.requireNonNull(name);
        this.floorIndex = floorIndex;
        this.totalCapacity = totalCapacity;
    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public int getTotalCapacity() {
        return totalCapacity;
    }

    @Override
    public int getFilledCapacity() {
        return population.stream().mapToInt(Person::getSpaceTaken).sum();
    }

    @Override
    public int getFreeCapacity() {
        return getTotalCapacity() - getFilledCapacity();
    }

    @Override
    public boolean isFull() {
        return getFreeCapacity() > Person.AVERAGE_CAPACITY_TAKEN;
    }

    public boolean canMove() {
        return false;
    }

    public Attitude getAttitude() {
        return attitude;
    }

    public void setAttitude(final Attitude attitude) {
        this.attitude = attitude;
    }

    @Override
    public double getFloorIndex() {
        return floorIndex;
    }

    protected void setFloorIndex(final double floorIndex) {
        if (canMove()) {
            this.floorIndex = floorIndex;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void remove(final Person person) {
        this.population.remove(person);
    }

    @Override
    public void accept(final Person person) {
        population.add(person);
        person.updateLocation(this);
    }

    @Override
    public Set<Person> population() {
        return Collections.unmodifiableSortedSet(new TreeSet<>(population));
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void movePeople(final AbstractPlace to, final Person person) {
        if (hasPerson(person) && isAdjacent(to) && to.getFreeCapacity() > person.getSpaceNeeded()) {
            doMove(to, person);
        }
    }

    protected boolean isAdjacent(final AbstractPlace other) {
        return this.building.equals(other.building)
                && this.floorIndex == Objects.requireNonNull(other).floorIndex;
    }

    private boolean hasPerson(final Person person) {
        return population.contains(Objects.requireNonNull(person));
    }

    private void doMove(final AbstractPlace to, final Person person) {
        if (to.getFreeCapacity() > person.getSpaceNeeded() && to.getFreeCapacity() > person.getSpaceTaken()) {
            to.accept(person);
            this.remove(person);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractPlace)) {
            return false;
        }
        final AbstractPlace place = (AbstractPlace) o;
        return index == place.index && building.equals(place.building);
    }

    @Override
    public int hashCode() {
        return Objects.hash(building, index);
    }

    @Override
    @ExcludeIdeGeneratedToStringFromJacoco
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("building=" + building)
                .add("index=" + index)
                .add("name=" + name)
                .toString();
    }
}
