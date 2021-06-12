package com.github.nagyesta.demo.nla.state.building.factory;

import com.github.nagyesta.demo.nla.state.building.Building;
import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.control.ElevatorController;

import java.util.function.Function;

public interface BuildingFactory {

    static Building largeCommercialBuilding(final Function<BuildingDimensions, ElevatorController> elevatorControllerFunction) {
        return new LargeCommercialBuildingFactory().newBuildingInstance(elevatorControllerFunction);
    }

    static Building socialDistancedCommercialBuilding(final Function<BuildingDimensions, ElevatorController> elevatorControllerFunction) {
        return new SocialDistancedCommercialBuildingFactory().newBuildingInstance(elevatorControllerFunction);
    }

    static Building tinyBuilding(final Function<BuildingDimensions, ElevatorController> elevatorControllerFunction) {
        return new TinyBuildingFactory().newBuildingInstance(elevatorControllerFunction);
    }

    /**
     * Creates a new instance of the building.
     *
     * @param elevatorControllerFunction The function converting building dimensions to elevator controller
     * @return building
     */
    Building newBuildingInstance(Function<BuildingDimensions, ElevatorController> elevatorControllerFunction);

}
