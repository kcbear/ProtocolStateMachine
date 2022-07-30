package example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ProtocolState {

    Initialising {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return true;
        }

        @Override
        public void apply(int hostId) {
            // noop
        }
    },
    StopAsMaster {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return currentState == Master;
        }

        @Override
        public void apply(int hostId) {
            setMasterIds(hostId);
        }
    },
    StopAsSlave {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return currentState == Slave;
        }

        @Override
        public void apply(int hostId) {
            setSlaveIds(hostId);
        }
    },
    SwitchToMaster {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return currentState == Slave
                    || currentState == StopAsSlave
                    || currentState == Initialising;
        }

        @Override
        public void apply(int hostId) {
            setMasterIds(hostId);
        }
    },
    SwitchToSlave {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return currentState == Master
                    || currentState == StopAsMaster
                    || currentState == Initialising;
        }
        @Override
        public void apply(int hostId) {
            setSlaveIds(hostId);
        }
    },
    StartAsMaster {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return currentState == Slave
                    || currentState == SwitchToMaster
                    || currentState == Initialising;
        }
        @Override
        public void apply(int hostId) {
            setMasterIds(hostId);
        }
    },
    StartAsSlave {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return currentState == Master
                    || currentState == SwitchToSlave
                    || currentState == Initialising;
        }
        @Override
        public void apply(int hostId) {
            setSlaveIds(hostId);
        }
    },
    Master {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return currentState == SwitchToSlave
                    || currentState == StartAsMaster
                    || currentState == Initialising;
        }
        @Override
        public void apply(int hostId) {
            setMasterIds(hostId);
        }
    },
    Slave {
        @Override
        public boolean isValidTargetState(ProtocolState currentState) {
            return currentState == SwitchToMaster
                    || currentState == StartAsSlave
                    || currentState == Initialising;
        }
        @Override
        public void apply(int hostId) {
            setSlaveIds(hostId);
        }
    };

    private final Logger logger = LogManager.getLogger(this.getClass());
    private static final int UNKNOWN = Integer.MIN_VALUE;
    protected int hostId = UNKNOWN;
    protected int masterId = UNKNOWN;
    protected int slaveId = UNKNOWN;

    public ProtocolState nextState(int hostId, final ProtocolState targetState) {
        if (!targetState.isValidTargetState(this)) {
            logger.info("current state [{}] is not applicable for [{}]", this.name(), targetState.name());
            return this;
        }
        return setTargetState(hostId, targetState);
    }

    public void logStateChange(final ProtocolState targetState) {
        logger.info("Changed as targetState: [{}] from [{}]", targetState, this);
    }

    public abstract boolean isValidTargetState(ProtocolState currentState);

    public abstract void apply(int hostId);

    public ProtocolState setTargetState(final int hostId, ProtocolState targetState) {
        targetState.apply(hostId);
        logStateChange(targetState);
        return targetState;
    }

    protected void setMasterIds(int hostId) {
        this.hostId = hostId;
        this.masterId = hostId;
        this.slaveId = (hostId == 1) ? 2 : 1;
    }

    protected void setSlaveIds(int hostId) {
        this.hostId = hostId;
        this.masterId = (hostId == 2) ? 1 : 2;
        this.slaveId = hostId;
    }

    @Override
    public String toString() {
        return this.name() + "{" +
                "hostId=" + this.hostId +
                ", masterId=" + this.masterId +
                ", slaveId=" + this.slaveId +
                '}';
    }


    public int getHostId() {
        return hostId;
    }

    public int getMasterId() {
        return masterId;
    }

    public int getSlaveId() {
        return slaveId;
    }
}
