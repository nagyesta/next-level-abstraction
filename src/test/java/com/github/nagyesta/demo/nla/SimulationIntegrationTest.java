package com.github.nagyesta.demo.nla;

import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.factory.LargeCommercialBuildingFactory;
import com.github.nagyesta.demo.nla.state.building.factory.TinyBuildingFactory;
import com.github.nagyesta.demo.nla.state.control.CustomElevatorController;
import com.github.nagyesta.demo.nla.state.control.ElevatorController;
import com.github.nagyesta.demo.nla.state.control.InfiniteMonkeysElevatorController;
import com.github.nagyesta.demo.nla.state.control.InfiniteMonkeysWithShortcutsElevatorController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.nagyesta.demo.nla.Simulation.*;

class SimulationIntegrationTest {

    static final String CONTROLLER_INFINITE_MONKEYS = "--monkeys";
    static final String BUILDING_LARGE = "--large";

    private static Stream<Arguments> argsProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(new String[0],
                        InfiniteMonkeysElevatorController.class,
                        LargeCommercialBuildingFactory.FLOORS,
                        LargeCommercialBuildingFactory.ELEVATORS,
                        LargeCommercialBuildingFactory.ELEVATOR_MAX_OCCUPANTS,
                        SLEEP_TIME_DEFAULT))
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS_WITH_SHORTCUTS, BUILDING_LARGE},
                        InfiniteMonkeysWithShortcutsElevatorController.class,
                        LargeCommercialBuildingFactory.FLOORS,
                        LargeCommercialBuildingFactory.ELEVATORS,
                        LargeCommercialBuildingFactory.ELEVATOR_MAX_OCCUPANTS,
                        SLEEP_TIME_DEFAULT))
                .add(Arguments.of(new String[]{CONTROLLER_CUSTOM, BUILDING_TINY, SPEED_FAST, CONTROLLER_INFINITE_MONKEYS},
                        CustomElevatorController.class,
                        TinyBuildingFactory.FLOORS,
                        TinyBuildingFactory.ELEVATORS,
                        TinyBuildingFactory.ELEVATOR_MAX_OCCUPANTS,
                        SLEEP_TIME_FAST))
                .add(Arguments.of(new String[]{CONTROLLER_CUSTOM, BUILDING_TINY, SPEED_SLOW},
                        CustomElevatorController.class,
                        TinyBuildingFactory.FLOORS,
                        TinyBuildingFactory.ELEVATORS,
                        TinyBuildingFactory.ELEVATOR_MAX_OCCUPANTS,
                        SLEEP_TIME_SLOW))
                .add(Arguments.of(new String[]{BUILDING_TINY, SPEED_ULTRA_FAST, CONTROLLER_INFINITE_MONKEYS},
                        InfiniteMonkeysElevatorController.class,
                        TinyBuildingFactory.FLOORS,
                        TinyBuildingFactory.ELEVATORS,
                        TinyBuildingFactory.ELEVATOR_MAX_OCCUPANTS,
                        SLEEP_TIME_ULTRA_FAST))
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS_WITH_SHORTCUTS, BUILDING_LARGE, SPEED_INSANE},
                        InfiniteMonkeysWithShortcutsElevatorController.class,
                        LargeCommercialBuildingFactory.FLOORS,
                        LargeCommercialBuildingFactory.ELEVATORS,
                        LargeCommercialBuildingFactory.ELEVATOR_MAX_OCCUPANTS,
                        SLEEP_TIME_INSANE))
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS_WITH_SHORTCUTS, BUILDING_LARGE, SPEED_ULTRA_SLOW},
                        InfiniteMonkeysWithShortcutsElevatorController.class,
                        LargeCommercialBuildingFactory.FLOORS,
                        LargeCommercialBuildingFactory.ELEVATORS,
                        LargeCommercialBuildingFactory.ELEVATOR_MAX_OCCUPANTS,
                        SLEEP_TIME_ULTRA_SLOW))
                .build();
    }

    private static Stream<Arguments> integrationTestArgProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS, BUILDING_LARGE, SPEED_INSANE}, 1435, 876))
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS_WITH_SHORTCUTS, BUILDING_LARGE, SPEED_INSANE}, 1363, 876))
                .add(Arguments.of(new String[]{CONTROLLER_ONLY_PRESSED, BUILDING_LARGE, SPEED_INSANE}, 858, 876))
                .add(Arguments.of(new String[]{CONTROLLER_LOAD_BALANCING, BUILDING_LARGE, SPEED_INSANE}, 813, 876))
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS, BUILDING_TINY, SPEED_INSANE}, 585, 38))
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS_WITH_SHORTCUTS, BUILDING_TINY, SPEED_INSANE}, 585, 38))
                .add(Arguments.of(new String[]{CONTROLLER_ONLY_PRESSED, BUILDING_TINY, SPEED_INSANE}, 531, 38))
                .add(Arguments.of(new String[]{CONTROLLER_LOAD_BALANCING, BUILDING_TINY, SPEED_INSANE}, 536, 38))
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS, BUILDING_EMPTY, SPEED_INSANE}, 775, 20))
                .add(Arguments.of(new String[]{CONTROLLER_INFINITE_MONKEYS_WITH_SHORTCUTS, BUILDING_EMPTY, SPEED_INSANE}, 725, 20))
                .add(Arguments.of(new String[]{CONTROLLER_ONLY_PRESSED, BUILDING_EMPTY, SPEED_INSANE}, 505, 20))
                .add(Arguments.of(new String[]{CONTROLLER_LOAD_BALANCING, BUILDING_EMPTY, SPEED_INSANE}, 486, 20))
                .build();
    }

    @ParameterizedTest
    @MethodSource("argsProvider")
    void testParseShouldParseSleepTimesBuildingAndElevatorControllerNameAccuratelyWhenCalled(
            final String[] args,
            final Class<? extends ElevatorController> controllerClass,
            final int floors,
            final int elevators,
            final int maxOccupants,
            final long sleepTime) {

        //given
        final BuildingDimensions expectedDimensions = new BuildingDimensions(floors, elevators, maxOccupants);

        //when
        final Simulation actual = Simulation.parse(args);

        //then
        Assertions.assertEquals(expectedDimensions, actual.getBuilding().getBuildingDimensions());
        Assertions.assertEquals(controllerClass, actual.getBuilding().getController().getClass());
        Assertions.assertEquals(sleepTime, actual.getSleepTime());
    }

    @ParameterizedTest
    @MethodSource("integrationTestArgProvider")
    void testSimpleSimulationShouldRunForGivenAmountOfStepsWhenExecuted(
            final String[] args, final int expectedTurns, final int expectedTrips) {
        //given
        final Simulation underTest = Simulation.parseHeadless(args);

        //when
        underTest.run();

        //then
        Assertions.assertTrue(underTest.getBuilding().stockRoom().allPresent());
        Assertions.assertEquals(expectedTurns, underTest.getBuilding().getLastTurn());
        Assertions.assertEquals(expectedTrips, underTest.getBuilding().totalTrips());
    }
}
