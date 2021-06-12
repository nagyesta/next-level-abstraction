package com.github.nagyesta.demo.nla.state.building;

import com.github.nagyesta.demo.nla.state.elevator.ElevatorStatus;

public enum Attitude {
    POSITIVE {
        @Override
        public ElevatorStatus slowMove() {
            return ElevatorStatus.MOVING_UP_SLOW;
        }

        @Override
        public ElevatorStatus fastMove() {
            return ElevatorStatus.MOVING_UP_FAST;
        }
    },
    NEGATIVE {
        @Override
        public ElevatorStatus slowMove() {
            return ElevatorStatus.MOVING_DOWN_SLOW;
        }

        @Override
        public ElevatorStatus fastMove() {
            return ElevatorStatus.MOVING_DOWN_FAST;
        }
    }, CONSTANT;

    public static Attitude forMovement(final double from, final double to) {
        if (from == to) {
            return CONSTANT;
        } else if (from < to) {
            return POSITIVE;
        } else {
            return NEGATIVE;
        }
    }

    public ElevatorStatus slowMove() {
        return ElevatorStatus.STOPPED;
    }

    public ElevatorStatus fastMove() {
        return ElevatorStatus.STOPPED;
    }
}
