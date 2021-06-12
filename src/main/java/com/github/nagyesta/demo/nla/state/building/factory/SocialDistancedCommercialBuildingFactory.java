package com.github.nagyesta.demo.nla.state.building.factory;

import com.github.nagyesta.demo.nla.state.building.Building;
import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.StockRoom;
import com.github.nagyesta.demo.nla.state.control.ElevatorController;
import com.github.nagyesta.demo.nla.state.people.Person;

import java.util.Set;
import java.util.function.Function;

public class SocialDistancedCommercialBuildingFactory extends GenericBuildingFactory implements BuildingFactory {

    public static final int FLOORS = 12;
    public static final int ELEVATORS = 3;
    public static final int ELEVATOR_MAX_OCCUPANTS = 8;
    public static final int OFFICE_WORKER_COUNT = 5;

    @Override
    public Building newBuildingInstance(final Function<BuildingDimensions, ElevatorController> elevatorController) {
        final BuildingDimensions dimensions = new BuildingDimensions(FLOORS, ELEVATORS, ELEVATOR_MAX_OCCUPANTS);
        final Function<Building, StockRoom> buildingStockRoomFunction = StockRoom.withPeople(getPeople(dimensions));
        return new Building(elevatorController, buildingStockRoomFunction, dimensions);
    }

    private Set<Person> getPeople(final BuildingDimensions buildingDimensions) {
        return generatePeople(buildingDimensions, OFFICE_WORKER_COUNT, ELEVATORS, 0);
    }

    @Override
    protected int meetingFactor(final int index) {
        return index % 2;
    }

}
