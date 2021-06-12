package com.github.nagyesta.demo.nla.state.people.special;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.people.Action;
import com.github.nagyesta.demo.nla.state.people.GenericPerson;
import com.github.nagyesta.demo.nla.state.people.Person;
import com.github.nagyesta.demo.nla.state.people.SpaceRequirements;
import com.github.nagyesta.demo.nla.state.program.PersonalBehaviorBuilder;
import com.github.nagyesta.demo.nla.state.program.Step;

import java.util.Objects;
import java.util.Queue;

import static com.github.nagyesta.demo.nla.state.program.PersonalBehaviorBuilder.WORKDAY;
import static com.github.nagyesta.demo.nla.state.program.PersonalBehaviorBuilder.WORKSTART;

public class PersonFactory {

    public static final int PACKAGE_DIMENSIONS = 150;
    private final BuildingDimensions buildingDimensions;

    public PersonFactory(final BuildingDimensions buildingDimensions) {
        this.buildingDimensions = Objects.requireNonNull(buildingDimensions);
    }

    public Person officeWorker(final int index, final int workFloorIndex, final int meetings) {
        final Queue<Step<Action>> behavior = PersonalBehaviorBuilder.builder(buildingDimensions)
                .enterAfter( WORKSTART + (workFloorIndex + index) % 30)
                .waitAndMoveTo(1, workFloorIndex)
                .addMeetings(workFloorIndex, meetings)
                .waitAndMoveTo(1 + index % 3, 0)
                .waitAndLeave(1 + index % 2)
                .build();
        final SpaceRequirements spaceRequirements = genericSpaceRequirement(index, 0);
        return instantiate(index, behavior, spaceRequirements);
    }

    public Person cleaningCrewMember(final int index, final int workFloorIndex) {
        final Queue<Step<Action>> behavior = PersonalBehaviorBuilder.builder(buildingDimensions)
                .enterAfter(1 + (workFloorIndex + index) % 2)
                .waitAndMoveTo(1, workFloorIndex)
                .waitAndMoveTo(1 + index % 3, 0)
                .waitAndLeave(1)
                .build();
        final SpaceRequirements spaceRequirements = genericSpaceRequirement(index, PACKAGE_DIMENSIONS);
        return new GenericPerson(behavior, spaceRequirements);
    }

    public Person deliveryPerson(final int index, final int workFloorIndex) {
        final Queue<Step<Action>> behavior = PersonalBehaviorBuilder.builder(buildingDimensions)
                .enterAfter(WORKSTART + (workFloorIndex + index) % 2 + WORKDAY / 2)
                .waitAndMoveTo(1, workFloorIndex)
                .waitAndMoveTo(1 + index % 3, 0)
                .waitAndLeave(1)
                .build();
        final SpaceRequirements spaceRequirements = deliverySpaceRequirements(index, workFloorIndex);
        return new GenericPerson(behavior, spaceRequirements);
    }

    private GenericPerson instantiate(final int index, final Queue<Step<Action>> behavior, final SpaceRequirements spaceRequirements) {
        if (index % 10 == 0) {
            return new BadPerson(behavior, spaceRequirements);
        } else if (index % 5 == 0) {
            return new LoneWolf(behavior, spaceRequirements);
        } else {
            return new GenericPerson(behavior, spaceRequirements);
        }
    }

    private PackageUpSpaceRequirements deliverySpaceRequirements(final int index, final int workFloorIndex) {
        return new PackageUpSpaceRequirements(
                regularPersonCapacity(index) + PACKAGE_DIMENSIONS,
                regularPersonCapacity(index),
                workFloorIndex);
    }

    private SpaceRequirements genericSpaceRequirement(final int index, final int tools) {
        return new SpaceRequirements(regularPersonCapacity(index) + tools);
    }

    private int regularPersonCapacity(final int index) {
        return Person.AVERAGE_CAPACITY_TAKEN - 20 + index % 40;
    }
}
