package com.github.nagyesta.demo.nla.state.building;

import com.github.nagyesta.demo.nla.annotation.ExcludeIdeGeneratedToStringFromJacoco;
import com.github.nagyesta.demo.nla.state.control.ElevatorController;
import com.github.nagyesta.demo.nla.state.elevator.Elevator;
import com.github.nagyesta.demo.nla.state.elevator.ElevatorCar;
import com.github.nagyesta.demo.nla.state.elevator.ProgrammableElevator;
import com.github.nagyesta.demo.nla.state.elevator.SlowElevatorCar;
import com.github.nagyesta.demo.nla.state.people.Person;
import com.github.nagyesta.demo.nla.state.people.PersonStepCounter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Building implements TurnBased {

    public static final int GROUND_FLOOR = 0;
    public static final int STOCKROOM_FLOOR_INDEX = -1;
    private final UUID id = UUID.randomUUID();
    private final SortedMap<Integer, Elevator> elevators = new TreeMap<>();
    private final ElevatorController controller;
    private final SortedMap<Integer, Floor> floors = new TreeMap<>();
    private final StockRoom stockRoom;
    private final BuildingDimensions buildingDimensions;
    private int lastTurn = 0;

    public Building(final Function<BuildingDimensions, ElevatorController> elevatorControllerFunction,
                    final Function<Building, StockRoom> stockRoomFunction,
                    final BuildingDimensions buildingDimensions) {
        this.controller = Objects.requireNonNull(elevatorControllerFunction).apply(Objects.requireNonNull(buildingDimensions));
        this.stockRoom = Objects.requireNonNull(stockRoomFunction).apply(this);
        this.buildingDimensions = buildingDimensions;
        IntStream.range(0, this.buildingDimensions.getFloors())
                .forEachOrdered(i -> this.floors.put(i, new Floor(this, i, i == 0 ? " G" : String.format("%2d", i))));
        IntStream.range(0, buildingDimensions.getElevators())
                .forEachOrdered(i -> {
                    final String name = String.valueOf((char) ('A' + (char) i));
                    final int totalCapacity = buildingDimensions.getElevatorCapacity();
                    if (i % 2 == 0) {
                        this.elevators.put(i, new ElevatorCar(this, i, name, totalCapacity));
                    } else {
                        this.elevators.put(i, new SlowElevatorCar(this, i, name, totalCapacity));
                    }
                });
    }

    public Floor floor(final int index) {
        return floors.get(index);
    }

    public Elevator elevator(final int index) {
        return elevators.get(index);
    }

    public StockRoom stockRoom() {
        return stockRoom;
    }

    public Set<Elevator> elevators() {
        return Collections.unmodifiableSet(new HashSet<>(elevators.values()));
    }

    public Set<Floor> floors() {
        return Collections.unmodifiableSet(new HashSet<>(floors.values()));
    }

    public BuildingDimensions getBuildingDimensions() {
        return buildingDimensions;
    }

    public ElevatorController getController() {
        return controller;
    }

    @Override
    public void nextTurn(final int turnIndex) {
        this.elevators.values().forEach(e -> e.nextTurn(turnIndex));
        this.floors.values().forEach(f -> f.nextTurn(turnIndex));
        this.stockRoom.nextTurn(turnIndex);
        controller.program(getFloorStats(), getElevatorsByIdleStatus());
        lastTurn = turnIndex;
    }

    public int totalTrips() {
        final int fromStockRoom = stockRoom.population().stream()
                .map(Person::getStepCounter)
                .mapToInt(PersonStepCounter::getTripsCompleted).sum();
        final int fromElevators = elevators.values().stream()
                .map(Elevator::population)
                .flatMap(Collection::stream)
                .map(Person::getStepCounter)
                .mapToInt(PersonStepCounter::getTripsCompleted).sum();
        final int fromFloors = floors.values().stream()
                .map(Floor::population)
                .flatMap(Collection::stream)
                .map(Person::getStepCounter)
                .mapToInt(PersonStepCounter::getTripsCompleted).sum();
        return fromStockRoom + fromElevators + fromFloors;
    }

    public int getLastTurn() {
        return lastTurn;
    }

    private FloorStats getFloorStats() {
        return new FloorStats(this.floors.values());
    }

    private Map<Boolean, List<ProgrammableElevator>> getElevatorsByIdleStatus() {
        return this.elevators.values().stream()
                .map(ProgrammableElevator.class::cast)
                .collect(Collectors.groupingBy(ProgrammableElevator::isIdle));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Building)) {
            return false;
        }
        final Building building = (Building) o;
        return id.equals(building.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    @ExcludeIdeGeneratedToStringFromJacoco
    public String toString() {
        return new StringJoiner(", ", Building.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .toString();
    }
}
