package com.github.nagyesta.demo.nla.state.building;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FloorStatsTest {

    public static final int UP_MOD = 2;
    public static final int DOWN_MOD = 3;
    public static final int CONST_MOD = 5;

    @Test
    void testWaitingCountersShouldReturnFloorsWithMatchingGroupsWhenCalled() {
        //given
        final SortedSet<Integer> expectedUp = calculateExpected(UP_MOD);
        final SortedSet<Integer> expectedDown = new TreeSet<>(Comparator.reverseOrder());
        expectedDown.addAll(calculateExpected(DOWN_MOD));
        final SortedSet<Integer> expectedNotEmpty = calculateExpected(CONST_MOD);
        final List<Floor> floors = IntStream.range(0, 10)
                .mapToObj(i -> {
                    final Floor mock = mock(Floor.class);
                    when(mock.stats()).thenCallRealMethod();
                    when(mock.countPeople(eq(Attitude.POSITIVE))).thenReturn(i % UP_MOD);
                    when(mock.countPeople(eq(Attitude.NEGATIVE))).thenReturn(i % DOWN_MOD);
                    when(mock.countPeople(eq(Attitude.CONSTANT))).thenReturn(i % CONST_MOD);
                    when(mock.getIndex()).thenReturn(i);
                    return mock;
                })
                .collect(Collectors.toList());
        final FloorStats underTest = new FloorStats(floors);

        //when
        final SortedSet<Integer> nonEmptyFloors = underTest.nonEmptyFloors();
        final SortedSet<Integer> waitingToGoDown = underTest.waitingToGoDown();
        final SortedSet<Integer> waitingToGoUp = underTest.waitingToGoUp();

        //then
        Assertions.assertIterableEquals(expectedNotEmpty, nonEmptyFloors);
        Assertions.assertIterableEquals(expectedDown, waitingToGoDown);
        Assertions.assertIterableEquals(expectedUp, waitingToGoUp);
    }

    private TreeSet<Integer> calculateExpected(final int modulo) {
        return IntStream.range(0, 10)
                .filter(i -> i % modulo > 0)
                .boxed()
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
