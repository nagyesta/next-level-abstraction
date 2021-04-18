package com.github.nagyesta.demo.nla.state;

public enum ElevatorStatus {
    DOOR_OPEN,
    STOPPED,
    MOVING {
        @Override
        public boolean isAllowedWhen(ElevatorMovement movement) {
            return movement == ElevatorMovement.DOWN_SLOW || movement == ElevatorMovement.DOWN_FAST
                    || movement == ElevatorMovement.UP_SLOW || movement == ElevatorMovement.UP_FAST;
        }
    };

    public boolean isAllowedWhen(ElevatorMovement movement) {
        return movement == ElevatorMovement.STOPPED;
    }
}
