package com.github.nagyesta.demo.nla.state.elevator;

public enum ElevatorStatus {
    DOOR_OPEN(0.0D) {
        @Override
        public boolean isDoorOpen() {
            return true;
        }
    },
    STOPPED(0.0D),
    MOVING_DOWN_SLOW(-0.5D) {
        @Override
        public boolean isMovingSlow() {
            return true;
        }
    },
    MOVING_UP_SLOW(0.5D) {
        @Override
        public boolean isMovingSlow() {
            return true;
        }
    },
    MOVING_DOWN_FAST(-1.0D) {
        @Override
        public boolean isMovingFast() {
            return true;
        }
    },
    MOVING_UP_FAST(1.0D) {
        @Override
        public boolean isMovingFast() {
            return true;
        }
    };

    private final double travelPerTurn;

    ElevatorStatus(final double travelPerTurn) {
        this.travelPerTurn = travelPerTurn;
    }

    public double getTravelPerTurn() {
        return travelPerTurn;
    }

    public boolean isDoorOpen() {
        return false;
    }

    public boolean isMoving() {
        return isMovingSlow() || isMovingFast();
    }

    public boolean isMovingSlow() {
        return false;
    }

    public boolean isMovingFast() {
        return false;
    }
}
