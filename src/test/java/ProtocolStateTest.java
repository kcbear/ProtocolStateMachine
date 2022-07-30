import example.ProtocolState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static example.ProtocolState.*;

public class ProtocolStateTest {
    private ProtocolState state;

    @BeforeEach
    public void setup() {
        state = Initialising;
    }

    @Test
    public void test() {
        // from initialising to Master
        state = state.nextState(1, SwitchToMaster);
        assertState(SwitchToMaster.name(),1, 1, 2, state);

        state = state.nextState(1, StartAsSlave);
        assertState(SwitchToMaster.name(),1, 1, 2, state);

        state = state.nextState(1, StartAsMaster);
        assertState(StartAsMaster.name(),1, 1, 2, state);

        state = state.nextState(1, Master);
        assertState(Master.name(),1, 1, 2, state);
    }

    @Test
    public void fromMasterToSlave() {
        // from master to slave
        state = state.nextState(1, Master);
        assertState(Master.name(),1, 1, 2, state);

        state = state.nextState(1, StopAsMaster);
        assertState(StopAsMaster.name(),1, 1, 2, state);

        state = state.nextState(1, SwitchToSlave);
        assertState(SwitchToSlave.name(),1, 2, 1, state);

        state = state.nextState(1, StartAsSlave);
        assertState(StartAsSlave.name(),1, 2, 1, state);

        state = state.nextState(1, Slave);
        assertState(Slave.name(),1, 2, 1, state);
    }

    @Test
    public void fromSlaveToMaster() {
        // from slave to master
        state = state.nextState(1, Slave);
        assertState(Slave.name(),1, 2, 1, state);

        state = state.nextState(1, StopAsSlave);
        assertState(StopAsSlave.name(),1, 2, 1, state);

        state = state.nextState(1, SwitchToMaster);
        assertState(SwitchToMaster.name(),1, 1, 2, state);

        state = state.nextState(1, StartAsMaster);
        assertState(StartAsMaster.name() , 1, 1, 2, state);

        state = state.nextState(1, Master);
        assertState(Master.name() , 1, 1, 2, state);
    }

    private void assertState(String expectedName, int expectedHostId, int expectedMasterId, int expectedSlaveId, ProtocolState state) {
        Assertions.assertEquals(expectedName, state.name());
        Assertions.assertEquals(expectedHostId, state.getHostId());
        Assertions.assertEquals(expectedMasterId, state.getMasterId());
        Assertions.assertEquals(expectedSlaveId, state.getSlaveId());
    }


}
