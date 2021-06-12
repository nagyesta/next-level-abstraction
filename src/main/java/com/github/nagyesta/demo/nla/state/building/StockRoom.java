package com.github.nagyesta.demo.nla.state.building;

import com.github.nagyesta.demo.nla.state.people.Person;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class StockRoom extends AbstractPlace implements TurnBased {

    private final int fullPopulation;

    private StockRoom(final Building building, final Set<Person> population) {
        super(building, Building.STOCKROOM_FLOOR_INDEX, "Outside", 0, Integer.MAX_VALUE);
        Objects.requireNonNull(population).forEach(this::accept);
        this.fullPopulation = population.size();
    }

    public static Function<Building, StockRoom> withPeople(final Set<Person> population) {
        return building -> new StockRoom(building, population);
    }

    @Override
    public void nextTurn(final int turnIndex) {
        this.population().forEach(p -> p.nextTurn(turnIndex));
    }

    public boolean allPresent() {
        return this.fullPopulation == this.population().size()
                && this.population().stream().allMatch(Person::finished);
    }
}
