package com.github.nagyesta.demo.nla;

import com.github.nagyesta.demo.nla.model.world.World;
import com.github.nagyesta.demo.nla.state.building.Building;
import com.github.nagyesta.demo.nla.state.building.BuildingDimensions;
import com.github.nagyesta.demo.nla.state.building.factory.BuildingFactory;
import com.github.nagyesta.demo.nla.state.control.CustomElevatorController;
import com.github.nagyesta.demo.nla.state.control.ElevatorController;
import com.github.nagyesta.demo.nla.state.control.InfiniteMonkeysElevatorController;
import com.github.nagyesta.demo.nla.state.control.InfiniteMonkeysWithShortcutsElevatorController;
import com.github.nagyesta.demo.nla.state.control.LoadBalancingElevatorController;
import com.github.nagyesta.demo.nla.state.control.StopOnlyWhenPressedElevatorController;
import com.github.nagyesta.demo.nla.state.elevator.Elevator;
import com.github.nagyesta.demo.nla.state.people.Person;
import com.github.nagyesta.demo.nla.view.ConsoleConstants;
import com.github.nagyesta.demo.nla.view.ConsolePresenter;
import com.github.nagyesta.demo.nla.view.Stats;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

@Builder
@Data
public class Simulation implements Runnable {

    static final String CONTROLLER_INFINITE_MONKEYS_WITH_SHORTCUTS = "--shortcuts";
    static final String CONTROLLER_CUSTOM = "--custom";
    static final String CONTROLLER_ONLY_PRESSED = "--buttons";
    static final String CONTROLLER_LOAD_BALANCING = "--balanced";
    static final String BUILDING_TINY = "--tiny";
    static final String BUILDING_EMPTY = "--empty";
    static final String SPEED_ULTRA_FAST = "--ultra-fast";
    static final String SPEED_FAST = "--fast";
    static final String SPEED_SLOW = "--slow";
    static final String SPEED_ULTRA_SLOW = "--ultra-slow";
    static final String SPEED_INSANE = "--insane";
    static final String BENCHMARK = "--benchmark";
    static final long SLEEP_TIME_DEFAULT = 100L;
    static final long SLEEP_TIME_ULTRA_FAST = 15L;
    static final long SLEEP_TIME_FAST = 50L;
    static final long SLEEP_TIME_SLOW = 200L;
    static final long SLEEP_TIME_ULTRA_SLOW = 500L;
    static final long SLEEP_TIME_INSANE = 1L;
    private static final int FAST_MOVE_COST = 13;
    private static final int SLOW_MOVE_COST = 10;
    private static final Appendable NULL_APPENDABLE = new Appendable() {
        @Override
        public Appendable append(final CharSequence csq) throws IOException {
            return this;
        }

        @Override
        public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
            return this;
        }

        @Override
        public Appendable append(final char c) throws IOException {
            return this;
        }
    };
    private final long sleepTime;
    private final Building building;
    private final Appendable output;
    private final Appendable statsOutput;

    public static Simulation parse(final String[] args) {
        return Simulation.builder()
                .building(selectBuilding(args).apply(selectController(args)))
                .sleepTime(calculateSleepTime(args))
                .output(selectOutput(args))
                .statsOutput(System.out)
                .build();
    }

    protected static Simulation parseHeadless(final String[] args) {
        return Simulation.builder()
                .building(selectBuilding(args).apply(selectController(args)))
                .sleepTime(calculateSleepTime(args))
                .output(NULL_APPENDABLE)
                .statsOutput(NULL_APPENDABLE)
                .build();
    }

    private static Appendable selectOutput(final String[] args) {
        if (Arrays.stream(args).anyMatch(BENCHMARK::equalsIgnoreCase)) {
            return NULL_APPENDABLE;
        }
        return System.out;
    }

    private static Function<BuildingDimensions, ElevatorController> selectController(final String[] args) {
        if (Arrays.stream(args).anyMatch(CONTROLLER_INFINITE_MONKEYS_WITH_SHORTCUTS::equalsIgnoreCase)) {
            return InfiniteMonkeysWithShortcutsElevatorController::new;
        } else if (Arrays.stream(args).anyMatch(CONTROLLER_CUSTOM::equalsIgnoreCase)) {
            return CustomElevatorController::new;
        } else if (Arrays.stream(args).anyMatch(CONTROLLER_ONLY_PRESSED::equalsIgnoreCase)) {
            return StopOnlyWhenPressedElevatorController::new;
        } else if (Arrays.stream(args).anyMatch(CONTROLLER_LOAD_BALANCING::equalsIgnoreCase)) {
            return LoadBalancingElevatorController::new;
        } else {
            return InfiniteMonkeysElevatorController::new;
        }
    }

    private static Function<Function<BuildingDimensions, ElevatorController>, Building> selectBuilding(final String[] args) {
        if (Arrays.stream(args).anyMatch(BUILDING_TINY::equalsIgnoreCase)) {
            return BuildingFactory::tinyBuilding;
        } else if (Arrays.stream(args).anyMatch(BUILDING_EMPTY::equalsIgnoreCase)) {
            return BuildingFactory::socialDistancedCommercialBuilding;
        } else {
            return BuildingFactory::largeCommercialBuilding;
        }
    }

    private static long calculateSleepTime(final String[] args) {
        long sleepTime = SLEEP_TIME_DEFAULT;
        if (Arrays.stream(args).anyMatch(SPEED_ULTRA_FAST::equalsIgnoreCase)) {
            sleepTime = SLEEP_TIME_ULTRA_FAST;
        } else if (Arrays.stream(args).anyMatch(SPEED_FAST::equalsIgnoreCase)) {
            sleepTime = SLEEP_TIME_FAST;
        } else if (Arrays.stream(args).anyMatch(SPEED_SLOW::equalsIgnoreCase)) {
            sleepTime = SLEEP_TIME_SLOW;
        } else if (Arrays.stream(args).anyMatch(SPEED_ULTRA_SLOW::equalsIgnoreCase)) {
            sleepTime = SLEEP_TIME_ULTRA_SLOW;
        } else if (Arrays.stream(args).anyMatch(SPEED_INSANE::equalsIgnoreCase)) {
            sleepTime = SLEEP_TIME_INSANE;
        }
        return sleepTime;
    }

    @Override
    public void run() {
        try (final ConsolePresenter presenter = new ConsolePresenter(output)) {
            output.append(ConsoleConstants.CLEAR_SCREEN);
            animateFrames(presenter);
            printStats(statsOutput);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void animateFrames(final ConsolePresenter presenter) {
        presenter.renderFrame(World.fromBuilding(building));
        waitBeforeRefresh();
        for (int i = 1; i < 2000; i++) {
            building.nextTurn(i);
            presenter.renderFrame(World.fromBuilding(building));
            if (i > 1 && building.stockRoom().allPresent()) {
                break;
            }
            waitBeforeRefresh();
        }
    }

    private void printStats(final Appendable appendable) throws IOException {
        final Stats<Person> travel = new Stats<>(building.stockRoom().population(), p -> p.getStepCounter().getElevatorTravelTurns());
        final Stats<Person> wait = new Stats<>(building.stockRoom().population(), p -> p.getStepCounter().getElevatorWaitTurns());
        final Stats<Person> totalTime = new Stats<>(building.stockRoom().population(), p -> {
            final int travelTurns = p.getStepCounter().getElevatorTravelTurns();
            final int waitTurns = p.getStepCounter().getElevatorWaitTurns();
            return travelTurns + waitTurns;
        });
        final Stats<Elevator> slowMoves = new Stats<>(building.elevators(), e -> e.getStepCounter().getSlowMoves());
        final Stats<Elevator> fastMoves = new Stats<>(building.elevators(), e -> e.getStepCounter().getFastMoves());
        final Stats<Elevator> energySpent = new Stats<>(building.elevators(), e -> {
            final int fastCost = e.getStepCounter().getFastMoves() * FAST_MOVE_COST;
            final int slowCost = e.getStepCounter().getSlowMoves() * SLOW_MOVE_COST;
            return fastCost + slowCost;
        });
        appendable.append(building.stockRoom().allPresent() ? "Completed" : "Did not complete")
                .append(" in ").append(String.valueOf(building.getLastTurn())).append(" turns\n");
        appendable.append(travel.format("Person travel: min={1}, avg={2}, max={3}, sum={0}\n"));
        appendable.append(wait.format("Person wait  : min={1}, avg={2}, max={3}, sum={0}\n"));
        appendable.append(totalTime.format("Person total : min={1}, avg={2}, max={3}, sum={0}\n"));
        appendable.append(slowMoves.format("Slow moves   : min={1}, avg={2}, max={3}, sum={0}\n"));
        appendable.append(fastMoves.format("Fast moves   : min={1}, avg={2}, max={3}, sum={0}\n"));
        appendable.append(energySpent.format("Energy spent : min={1}, avg={2}, max={3}, sum={0}\n"));
    }

    private void waitBeforeRefresh() {
        try {
            Thread.sleep(sleepTime);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
