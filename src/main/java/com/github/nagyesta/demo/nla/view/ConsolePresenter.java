package com.github.nagyesta.demo.nla.view;

import com.github.nagyesta.demo.nla.model.world.Elevator;
import com.github.nagyesta.demo.nla.model.world.Floor;
import com.github.nagyesta.demo.nla.model.world.World;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.nagyesta.demo.nla.view.ConsoleConstants.*;

public class ConsolePresenter implements Presenter {

    public static final Function<Elevator, String> DOOR_NOT_APPLICABLE = e -> "";

    @Override
    public void renderFrame(Appendable appendable, World world) {
        appendHeader(appendable, world.getBuilding().getElevatorsByName().size());
        world.getBuilding().getFloorsByName().values().stream()
                .sorted(Comparator.comparing(Floor::getIndex).reversed())
                .forEachOrdered(f -> appendFloor(appendable, f, world.getBuilding().getElevatorsByName()));
        appendFooter(appendable, world);
    }

    private void appendHeader(Appendable appendable, int s) {
        try {
            appendable.append(auxiliaryRow(s, CANVAS_TOP_START, CANVAS_SHAFT_SEPARATOR, CANVAS_SHAFT, CANVAS_TOP_END));
            appendable.append(auxiliaryRow(s, BUILDING_TOP_START, BUILDING_TOP_SHAFT_SEPARATOR, BUILDING_TOP_SHAFT, BUILDING_TOP_END));
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void appendFloor(Appendable appendable, Floor floor, Map<String, Elevator> elevators) {
        try {
            appendable.append(
                    elevatorAwareRow(elevators,
                            BUILDING_FLOOR_START,
                            DOOR_NOT_APPLICABLE,
                            BUILDING_FLOOR_SHAFT_SEPARATOR,
                            elevatorShaftFunctionOnFloor(floor.getIndex()),
                            BUILDING_FLOOR_END));
            appendable.append(
                    elevatorAwareRow(elevators,
                            levelStart(floor),
                            elevatorDoorFunctionForFloor(floor.getIndex()),
                            BUILDING_LEVEL_SHAFT_SEPARATOR,
                            elevatorShaftFunctionOnLevel(floor.getIndex()),
                            String.format(BUILDING_LEVEL_END, floor.getName())));
            appendGroundFloorOptionally(appendable, floor, elevators);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private String levelStart(Floor floor) {
        String levelStart = BUILDING_LEVEL_START_EMPTY;
        if (floor.getOccupants() > 0) {
            levelStart = String.format(BUILDING_LEVEL_START_POPULATED, floor.getOccupants());
        }
        return levelStart;
    }

    private Function<Elevator, String> elevatorShaftFunctionOnLevel(int floorIndex) {
        return e -> {
            if (e.hasBottomOnFloor(floorIndex)) {
                return elevatorFloor(e);
            } else if (e.hasDoorOnFloor(floorIndex)) {
                return elevatorInterior(e);
            } else if (e.isBelowFloor(floorIndex)) {
                return ELEVATOR_CABLE;
            } else if (e.hasTopOnFloor(floorIndex)) {
                return CAR_CEILING;
            }
            return SHAFT_EMPTY;
        };
    }

    private Function<Elevator, String> elevatorShaftFunctionOnFloor(int floorIndex) {
        return e -> {
            if (e.hasDoorOnFloor(floorIndex + 1)) {
                return elevatorFloor(e);
            } else if (e.hasBottomOnFloor(floorIndex)) {
                return elevatorInterior(e);
            } else if (e.hasDoorOnFloor(floorIndex)) {
                return CAR_CEILING;
            } else if (e.isBelowFloor(floorIndex + 1)) {
                return ELEVATOR_CABLE;
            }
            return SHAFT_EMPTY;
        };
    }

    private Function<Elevator, String> elevatorDoorFunctionForFloor(int floorIndex) {
        return e -> {
            final String door;
            if (e.hasDoorOnFloor(floorIndex) && e.isDoorOpen()) {
                door = SHAFT_DOOR_OPEN;
            } else {
                door = SHAFT_DOOR_CLOSED;
            }
            return door;
        };
    }

    private void appendGroundFloorOptionally(Appendable appendable, Floor floor, Map<String, Elevator> elevators) throws IOException {
        if (floor.getIndex() == 0) {
            appendable.append(
                    elevatorAwareRow(elevators,
                            BUILDING_GROUND_FLOOR_START,
                            DOOR_NOT_APPLICABLE,
                            BUILDING_FLOOR_SHAFT_SEPARATOR,
                            elevatorShaftFunctionOnFloor(-1),
                            BUILDING_GROUND_FLOOR_END));
        }
    }

    private void appendFooter(Appendable appendable, World world) {
        try {
            Map<String, Elevator> elevators = world.getBuilding().getElevatorsByName();
            appendable.append(
                    elevatorAwareRow(elevators,
                            String.format(BUILDING_FOUNDATION_ROW_START, world.getSteps()),
                            DOOR_NOT_APPLICABLE,
                            BUILDING_FOUNDATION_ROW_SHAFT_SEPARATOR,
                            e -> String.format(BUILDING_FOUNDATION_ROW_SHAFT, e.getName()),
                            String.format(BUILDING_FOUNDATION_ROW_END, world.getTrips())));
            appendable.append(
                    auxiliaryRow(elevators.size(),
                            CANVAS_BOTTOM_START,
                            CANVAS_SHAFT_SEPARATOR,
                            CANVAS_SHAFT,
                            CANVAS_BOTTOM_END));
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private String elevatorInterior(Elevator e) {
        if (e.getOccupants() == 0) {
            return CAR_EMPTY;
        } else {
            return String.format(CAR_POPULATED, e.getOccupants());
        }
    }

    private String elevatorFloor(Elevator e) {
        switch (e.getStatus()) {
            case MOVING_UP_FAST:
                return CAR_FLOOR_UP_FAST;
            case MOVING_UP_SLOW:
                return CAR_FLOOR_UP_SLOW;
            case MOVING_DOWN_SLOW:
                return CAR_FLOOR_DOWN_SLOW;
            case MOVING_DOWN_FAST:
                return CAR_FLOOR_DOWN_FAST;
            case STOPPED:
            case DOOR_OPEN:
            default:
                return CAR_FLOOR_STOPPED;
        }
    }

    private String elevatorAwareRow(Map<String, Elevator> elevators,
                                    String start,
                                    Function<Elevator, String> doorFunction,
                                    String elevatorShaftSeparator,
                                    Function<Elevator, String> shaftFunction,
                                    String end) {
        return elevators.values().stream()
                .map(optionalDoorsWithElevatorShaft(doorFunction, shaftFunction))
                .collect(Collectors.joining(elevatorShaftSeparator, start, end + System.lineSeparator()));
    }

    private Function<Elevator, String> optionalDoorsWithElevatorShaft(Function<Elevator, String> doorFunction, Function<Elevator, String> shaftFunction) {
        return elevator -> {
            final String door = doorFunction.apply(elevator);
            return door + shaftFunction.apply(elevator) + door;
        };
    }

    private String auxiliaryRow(int numberOfElevators,
                                String start,
                                String elevatorShaftSeparator,
                                String elevatorShaft,
                                String end) {
        return IntStream.range(0, numberOfElevators)
                .mapToObj(i -> elevatorShaft)
                .collect(Collectors.joining(elevatorShaftSeparator, start, end + System.lineSeparator()));
    }
}
