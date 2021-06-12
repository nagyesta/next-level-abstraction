package com.github.nagyesta.demo.nla.view;

import com.github.nagyesta.demo.nla.model.world.Elevator;
import com.github.nagyesta.demo.nla.model.world.Floor;
import com.github.nagyesta.demo.nla.model.world.World;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_FLOOR_END;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_FLOOR_SHAFT_SEPARATOR;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_FLOOR_START;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_FOUNDATION_ROW_END;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_FOUNDATION_ROW_SHAFT;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_FOUNDATION_ROW_SHAFT_SEPARATOR;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_FOUNDATION_ROW_START;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_GROUND_FLOOR_END;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_GROUND_FLOOR_START;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_LEVEL_END;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_LEVEL_SHAFT_SEPARATOR;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_LEVEL_START_POPULATED;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_TOP_END;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_TOP_SHAFT;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_TOP_SHAFT_SEPARATOR;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.BUILDING_TOP_START;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CANVAS_BOTTOM_END;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CANVAS_BOTTOM_START;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CANVAS_SHAFT;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CANVAS_SHAFT_SEPARATOR;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CANVAS_TOP_END;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CANVAS_TOP_START;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CAR_CEILING;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CAR_EMPTY;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CAR_FLOOR_DOWN_FAST;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CAR_FLOOR_DOWN_SLOW;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CAR_FLOOR_STOPPED;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CAR_FLOOR_UP_FAST;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CAR_FLOOR_UP_SLOW;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.CAR_POPULATED;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.ELEVATOR_CABLE;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.GO_TO_TOP_LEFT;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.INFINITY;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.OCCUPANT_COUNTER_FORMAT;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.OCCUPANT_MISSING;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.SHAFT_DOOR_CLOSED;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.SHAFT_DOOR_OPEN;
import static com.github.nagyesta.demo.nla.view.ConsoleConstants.SHAFT_EMPTY;

public class ConsolePresenter implements Presenter, AutoCloseable {

    public static final Function<Elevator, String> DOOR_NOT_APPLICABLE = e -> "";

    private final Appendable appendable;

    public ConsolePresenter(final Appendable appendable) {
        this.appendable = Objects.requireNonNull(appendable);
    }

    @Override
    public void renderFrame(final World world) {
        appendHeader(world.getBuilding().getElevatorsByName().size());
        world.getBuilding().getFloorsByName().values().stream()
                .sorted(Comparator.comparing(Floor::getIndex).reversed())
                .forEachOrdered(f -> appendFloor(f, world.getBuilding().getElevatorsByName()));
        appendFooter(appendable, world);
    }

    private void appendHeader(final int s) {
        try {
            appendable.append(GO_TO_TOP_LEFT);
            appendable.append(auxiliaryRow(s, CANVAS_TOP_START, CANVAS_SHAFT_SEPARATOR, CANVAS_SHAFT, CANVAS_TOP_END));
            appendable.append(auxiliaryRow(s, BUILDING_TOP_START, BUILDING_TOP_SHAFT_SEPARATOR, BUILDING_TOP_SHAFT, BUILDING_TOP_END));
        } catch (final IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void appendFloor(final Floor floor, final Map<String, Elevator> elevators) {
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
            appendGroundFloorOptionally(floor, elevators);
        } catch (final IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private String levelStart(final Floor floor) {
        return String.format(BUILDING_LEVEL_START_POPULATED,
                format(floor.getOccupants()),
                format(floor.getOccupantsUp()),
                format(floor.getOccupantsDown()));
    }

    private String format(final int occupants) {
        return occupants > 0 ? formatPresent(occupants) : OCCUPANT_MISSING;
    }

    private String formatPresent(final int occupants) {
        return occupants > 99 ? INFINITY : String.format(OCCUPANT_COUNTER_FORMAT, occupants);
    }

    private Function<Elevator, String> elevatorShaftFunctionOnLevel(final int floorIndex) {
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

    private Function<Elevator, String> elevatorShaftFunctionOnFloor(final int floorIndex) {
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

    private Function<Elevator, String> elevatorDoorFunctionForFloor(final int floorIndex) {
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

    private void appendGroundFloorOptionally(final Floor floor, final Map<String, Elevator> elevators) throws IOException {
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

    private void appendFooter(final Appendable appendable, final World world) {
        try {
            final Map<String, Elevator> elevators = world.getBuilding().getElevatorsByName();
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
        } catch (final IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private String elevatorInterior(final Elevator e) {
        if (e.getOccupants() == 0) {
            return CAR_EMPTY;
        } else {
            return String.format(CAR_POPULATED, e.getOccupants());
        }
    }

    private String elevatorFloor(final Elevator e) {
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

    private String elevatorAwareRow(final Map<String, Elevator> elevators,
                                    final String start,
                                    final Function<Elevator, String> doorFunction,
                                    final String elevatorShaftSeparator,
                                    final Function<Elevator, String> shaftFunction,
                                    final String end) {
        return elevators.values().stream()
                .map(optionalDoorsWithElevatorShaft(doorFunction, shaftFunction))
                .collect(Collectors.joining(elevatorShaftSeparator, start, end + System.lineSeparator()));
    }

    private Function<Elevator, String> optionalDoorsWithElevatorShaft(final Function<Elevator, String> doorFunction, final Function<Elevator, String> shaftFunction) {
        return elevator -> {
            final String door = doorFunction.apply(elevator);
            return door + shaftFunction.apply(elevator) + door;
        };
    }

    private String auxiliaryRow(final int numberOfElevators,
                                final String start,
                                final String elevatorShaftSeparator,
                                final String elevatorShaft,
                                final String end) {
        return IntStream.range(0, numberOfElevators)
                .mapToObj(i -> elevatorShaft)
                .collect(Collectors.joining(elevatorShaftSeparator, start, end + System.lineSeparator()));
    }

    @Override
    public void close() throws Exception {
        if (appendable instanceof AutoCloseable) {
            ((AutoCloseable) this.appendable).close();
        }
    }
}
