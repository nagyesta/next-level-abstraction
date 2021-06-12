package com.github.nagyesta.demo.nla.state.people.special;

import com.github.nagyesta.demo.nla.state.people.SpaceRequirements;

public class PackageUpSpaceRequirements extends SpaceRequirements {

    private final int withoutPackage;
    private final int dropOffFloor;

    public PackageUpSpaceRequirements(final int withPackage, final int withoutPackage, final int dropOffFloor) {
        super(withPackage, withPackage);
        this.withoutPackage = withoutPackage;
        this.dropOffFloor = dropOffFloor;
    }

    @Override
    public void updateOnFloor(final double floorIndex) {
        if (floorIndex == dropOffFloor) {
            this.spaceTaken = withoutPackage;
            this.spaceNeeded = withoutPackage;
        }
    }
}
