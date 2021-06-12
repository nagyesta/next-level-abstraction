package com.github.nagyesta.demo.nla.state.people.special;

import com.github.nagyesta.demo.nla.state.building.Attitude;
import com.github.nagyesta.demo.nla.state.building.Floor;
import com.github.nagyesta.demo.nla.state.elevator.Elevator;
import com.github.nagyesta.demo.nla.state.elevator.ElevatorStatus;
import com.github.nagyesta.demo.nla.state.people.Action;
import com.github.nagyesta.demo.nla.state.people.GenericPerson;
import com.github.nagyesta.demo.nla.state.people.SpaceRequirements;
import com.github.nagyesta.demo.nla.state.program.FloorAwareStep;
import com.github.nagyesta.demo.nla.state.program.Step;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

class LoneWolfTest {

    public static final SpaceRequirements SPACE_REQUIREMENTS = new SpaceRequirements(1);
    public static final LinkedList<Step<Action>> GO_TO_FLOOR_3 = new LinkedList<>(
            Collections.singleton(new FloorAwareStep<>(Action.MOVE, 3)));
    public static final double CURRENT_FLOOR_INDEX = 1.0D;
    public static final double FLOOR_3 = 3.0D;

    @Test
    void testWantsToEnterShouldReturnFalseIfTheElevatorIsNotEmpty() {
        //given
        final LoneWolf underTest = new LoneWolf(GO_TO_FLOOR_3, SPACE_REQUIREMENTS);

        final Floor floor = mockFloor(underTest, CURRENT_FLOOR_INDEX);

        final Elevator elevator = mock(Elevator.class);
        when(elevator.getAttitude()).thenReturn(Attitude.POSITIVE);
        when(elevator.population()).thenReturn(Collections.singleton(mock(GenericPerson.class)));
        when(elevator.getStatus()).thenReturn(ElevatorStatus.DOOR_OPEN);
        when(elevator.isIdle()).thenReturn(false);
        when(elevator.getFilledCapacity()).thenReturn(1);

        //when
        final boolean actual = underTest.wantsToEnter(elevator);

        //then
        Assertions.assertFalse(actual);
        verifyFloor(underTest, floor);
        verify(elevator, atLeastOnce()).getAttitude();
        verify(elevator, atLeastOnce()).population();
        verify(elevator, atLeastOnce()).getFilledCapacity();
    }

    @Test
    void testWantsToEnterShouldReturnFalseIfTheElevatorIsNotGoingToTheRightDirection() {
        //given
        final LoneWolf underTest = new LoneWolf(GO_TO_FLOOR_3, SPACE_REQUIREMENTS);

        final Floor floor = mockFloor(underTest, CURRENT_FLOOR_INDEX);

        final GenericPerson alreadyInTheElevator = mock(GenericPerson.class);
        when(alreadyInTheElevator.attitude()).thenReturn(Attitude.NEGATIVE);

        final Elevator elevator = mock(Elevator.class);
        when(elevator.getAttitude()).thenReturn(Attitude.NEGATIVE);
        when(elevator.population()).thenReturn(Collections.singleton(alreadyInTheElevator));
        when(elevator.getStatus()).thenReturn(ElevatorStatus.DOOR_OPEN);
        when(elevator.isIdle()).thenReturn(false);
        when(elevator.getFilledCapacity()).thenReturn(0);

        //when
        final boolean actual = underTest.wantsToEnter(elevator);

        //then
        Assertions.assertFalse(actual);
        verifyFloor(underTest, floor);
        verify(alreadyInTheElevator, atLeastOnce()).attitude();
        verify(elevator, atLeastOnce()).getAttitude();
        verify(elevator, atLeastOnce()).population();
        verify(elevator, never()).isIdle();
        verify(elevator, never()).getFilledCapacity();
    }

    @Test
    void testWantsToEnterShouldReturnFalseIfThePersonDoesNotNeedAnElevator() {
        //given
        final LoneWolf underTest = new LoneWolf(GO_TO_FLOOR_3, SPACE_REQUIREMENTS);

        final Floor floor = mockFloor(underTest, FLOOR_3);

        final Elevator elevator = mock(Elevator.class);
        when(elevator.getAttitude()).thenReturn(Attitude.CONSTANT);
        when(elevator.getStatus()).thenReturn(ElevatorStatus.DOOR_OPEN);
        when(elevator.isIdle()).thenReturn(false);
        when(elevator.getFilledCapacity()).thenReturn(0);

        //when
        final boolean actual = underTest.wantsToEnter(elevator);

        //then
        Assertions.assertFalse(actual);
        verifyFloor(underTest, floor);
        verify(elevator, never()).getAttitude();
        verify(elevator, never()).population();
        verify(elevator, never()).isIdle();
        verify(elevator, never()).getFilledCapacity();
    }

    @Test
    void testWantsToEnterShouldReturnTrueIfAllCriteriaMatches() {
        //given
        final LoneWolf underTest = new LoneWolf(GO_TO_FLOOR_3, SPACE_REQUIREMENTS);

        final Floor floor = mockFloor(underTest, CURRENT_FLOOR_INDEX);

        final Elevator elevator = mock(Elevator.class);
        when(elevator.getAttitude()).thenReturn(Attitude.CONSTANT);
        when(elevator.population()).thenReturn(Collections.emptySet());
        when(elevator.getStatus()).thenReturn(ElevatorStatus.DOOR_OPEN);
        when(elevator.isIdle()).thenReturn(true);
        when(elevator.getFilledCapacity()).thenReturn(0);

        //when
        final boolean actual = underTest.wantsToEnter(elevator);

        //then
        Assertions.assertTrue(actual);
        verifyFloor(underTest, floor);
        verify(elevator, never()).getAttitude();
        verify(elevator, atLeastOnce()).population();
        verify(elevator, never()).isIdle();
        verify(elevator, atLeastOnce()).getFilledCapacity();
    }

    private Floor mockFloor(final LoneWolf underTest, final double floorIndex) {
        final Floor floor = mock(Floor.class);
        when(floor.getFloorIndex()).thenReturn(floorIndex);
        underTest.updateLocation(floor);
        return floor;
    }

    private void verifyFloor(final LoneWolf underTest, final Floor floor) {
        verify(floor, never()).accept(same(underTest));
        verify(floor, atLeastOnce()).getFloorIndex();
    }
}
