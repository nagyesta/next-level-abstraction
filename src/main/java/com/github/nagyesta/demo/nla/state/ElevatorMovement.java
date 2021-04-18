package com.github.nagyesta.demo.nla.state;

public enum ElevatorMovement {
    UP_FAST {
        @Override
        public boolean isValidTransitionTo(ElevatorMovement movement) {
            return movement == UP_SLOW;
        }
    },
    UP_SLOW {
        @Override
        public boolean isValidTransitionTo(ElevatorMovement movement) {
            return movement == UP_FAST || movement == STOPPED;
        }
    },
    STOPPED {
        @Override
        public boolean isValidTransitionTo(ElevatorMovement movement) {
            return movement == UP_SLOW || movement == DOWN_SLOW;
        }
    },
    DOWN_SLOW {
        @Override
        public boolean isValidTransitionTo(ElevatorMovement movement) {
            return movement == STOPPED || movement == DOWN_FAST;
        }
    },
    DOWN_FAST {
        @Override
        public boolean isValidTransitionTo(ElevatorMovement movement) {
            return movement == DOWN_SLOW;
        }
    };

    public abstract boolean isValidTransitionTo(ElevatorMovement movement);
}
