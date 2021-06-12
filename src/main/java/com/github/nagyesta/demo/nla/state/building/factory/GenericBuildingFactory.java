package com.github.nagyesta.demo.nla.state.building.factory;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.people.Person;
import com.github.nagyesta.demo.nla.state.people.special.PersonFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public abstract class GenericBuildingFactory {

    protected Set<Person> generatePeople(final BuildingDimensions buildingDimensions,
                                         final int officeWorkers,
                                         final int cleaningCrew,
                                         final int delivery) {
        final PersonFactory personFactory = new PersonFactory(buildingDimensions);
        final Set<Person> all = new HashSet<>();
        IntStream.range(0, officeWorkers)
                .mapToObj(index -> personFactory.officeWorker(index, uniformWorkFloor(buildingDimensions, index), meetingFactor(index)))
                .forEach(all::add);
        IntStream.range(0, cleaningCrew)
                .mapToObj(index -> personFactory.cleaningCrewMember(index, uniformWorkFloor(buildingDimensions, index)))
                .forEach(all::add);
        IntStream.range(0, delivery)
                .mapToObj(index -> personFactory.deliveryPerson(index, uniformWorkFloor(buildingDimensions, index)))
                .forEach(all::add);
        return all;
    }

    protected abstract int meetingFactor(int index);

    private int uniformWorkFloor(final BuildingDimensions buildingDimensions, final int index) {
        return 1 + index % buildingDimensions.getTopFloorIndex();
    }
}
