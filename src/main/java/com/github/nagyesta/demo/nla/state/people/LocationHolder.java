package com.github.nagyesta.demo.nla.state.people;

import com.github.nagyesta.demo.nla.annotation.ExcludeIdeGeneratedToStringFromJacoco;
import com.github.nagyesta.demo.nla.state.building.AbstractPlace;
import com.github.nagyesta.demo.nla.state.building.Building;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import static com.github.nagyesta.demo.nla.state.building.Building.STOCKROOM_FLOOR_INDEX;

public class LocationHolder {

    private AbstractPlace location;

    public void updateLocation(final Person person, final AbstractPlace place, final SpaceRequirements spaceRequirements) {
        final Optional<AbstractPlace> previous = Optional.ofNullable(location);
        location = Objects.requireNonNull(place);
        spaceRequirements.updateOnFloor(location.getFloorIndex());
        previous.ifPresent(p -> {
            p.remove(person);
            if (p.canMove()) {
                person.getStepCounter().recordTripComplete();
            }
        });
    }

    public void moveTo(final AbstractPlace place, final Person person) {
        Objects.requireNonNull(location).movePeople(place, person);
    }

    public double onFloor() {
        if (location != null) {
            return location.getFloorIndex();
        } else {
            return STOCKROOM_FLOOR_INDEX;
        }
    }

    public void enter(final Person person) {
        location.movePeople(location.getBuilding().floor(Building.GROUND_FLOOR), person);
    }

    public void leave(final Person person) {
        location.movePeople(location.getBuilding().stockRoom(), person);
    }

    public boolean isInElevator() {
        return location != null && location.canMove();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationHolder)) {
            return false;
        }
        final LocationHolder that = (LocationHolder) o;
        return Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    @ExcludeIdeGeneratedToStringFromJacoco
    public String toString() {
        return new StringJoiner(", ", LocationHolder.class.getSimpleName() + "[", "]")
                .add("location=" + location)
                .toString();
    }
}
