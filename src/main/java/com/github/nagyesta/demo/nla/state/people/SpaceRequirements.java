package com.github.nagyesta.demo.nla.state.people;

import com.github.nagyesta.demo.nla.annotation.ExcludeIdeGeneratedToStringFromJacoco;

import java.util.Objects;
import java.util.StringJoiner;

public class SpaceRequirements {

    protected int spaceNeeded;
    protected int spaceTaken;

    public SpaceRequirements(final int spaceTaken) {
        this(spaceTaken, spaceTaken);
    }

    public SpaceRequirements(final int spaceNeeded, final int spaceTaken) {
        this.spaceNeeded = spaceNeeded;
        this.spaceTaken = spaceTaken;
    }

    public int getSpaceNeeded() {
        return spaceNeeded;
    }

    public int getSpaceTaken() {
        return spaceTaken;
    }

    public void updateOnFloor(final double floorIndex) {

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaceRequirements)) {
            return false;
        }
        final SpaceRequirements that = (SpaceRequirements) o;
        return spaceNeeded == that.spaceNeeded && spaceTaken == that.spaceTaken;
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaceNeeded, spaceTaken);
    }

    @Override
    @ExcludeIdeGeneratedToStringFromJacoco
    public String toString() {
        return new StringJoiner(", ", SpaceRequirements.class.getSimpleName() + "[", "]")
                .add("spaceNeeded=" + spaceNeeded)
                .add("spaceTaken=" + spaceTaken)
                .toString();
    }
}
