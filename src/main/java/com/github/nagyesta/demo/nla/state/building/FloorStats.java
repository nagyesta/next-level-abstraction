package com.github.nagyesta.demo.nla.state.building;

import java.util.*;
import java.util.stream.Collectors;

public class FloorStats {

    private final Map<Integer, Map<Attitude, Integer>> stats;

    public FloorStats(final Collection<Floor> floors) {
        this(floors.stream().collect(Collectors.toMap(AbstractPlace::getIndex, Floor::stats)));
    }

    public FloorStats(final Map<Integer, Map<Attitude, Integer>> floorStats) {
        final Map<Integer, Map<Attitude, Integer>> computed = new TreeMap<>();
        floorStats.forEach((key, value) -> computed.put(key, Collections.unmodifiableMap(value)));
        this.stats = Collections.unmodifiableMap(computed);
    }

    public Map<Integer, Map<Attitude, Integer>> getStats() {
        return stats;
    }

    public SortedSet<Integer> nonEmptyFloors() {
        return floorsWithPeopleMatching(Attitude.CONSTANT);
    }

    public SortedSet<Integer> waitingToGoUp() {
        return floorsWithPeopleMatching(Attitude.POSITIVE);
    }

    public SortedSet<Integer> waitingToGoDown() {
        final TreeSet<Integer> sorted = new TreeSet<>(Comparator.reverseOrder());
        sorted.addAll(floorsWithPeopleMatching(Attitude.NEGATIVE));
        return sorted;
    }

    private TreeSet<Integer> floorsWithPeopleMatching(final Attitude attitude) {
        return stats.entrySet().stream()
                .filter(e -> e.getValue().getOrDefault(attitude, 0) > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
