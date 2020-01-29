package com.rbkmoney.machinarium;

import com.rbkmoney.machinarium.client.AutomatonClient;
import com.rbkmoney.machinarium.client.TBaseAutomatonClient;
import com.rbkmoney.machinarium.domain.CallResultData;
import com.rbkmoney.machinarium.domain.SignalResultData;
import com.rbkmoney.machinarium.domain.TMachine;
import com.rbkmoney.machinarium.domain.TMachineEvent;
import com.rbkmoney.machinarium.exception.MachineAlreadyExistsException;
import com.rbkmoney.machinarium.exception.NamespaceNotFoundException;
import com.rbkmoney.machinarium.handler.AbstractProcessorHandler;
import com.rbkmoney.machinegun.msgpack.Nil;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.*;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.Servlet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class MachinegunComplexTest extends AbstractTest {

    private final String automatonPath = "/v1/automaton";
    private final String processorPath = "/v1/processor";

    private AutomatonClient<Value, Value> aClient;
    private AutomatonSrv.Iface thriftClient;


    private Servlet processorServlet = createThriftRPCService(ProcessorSrv.Iface.class, new AbstractProcessorHandler<Value, Value>(Value.class, Value.class) {

        @Override
        protected SignalResultData<Value> processSignalInit(TMachine<Value> machine, Value args) {
            return new SignalResultData(Value.nl(new Nil()), Arrays.asList(args), new ComplexAction());
        }

        @Override
        protected SignalResultData<Value> processSignalTimeout(TMachine<Value> machine, List<TMachineEvent<Value>> tMachineEvents) {
            return new SignalResultData(Value.nl(new Nil()), Arrays.asList(Value.str("timeout")), new ComplexAction());
        }

        @Override
        protected CallResultData<Value> processCall(String namespace, String machineId, Value args, List<TMachineEvent<Value>> tMachineEvents) {
            return new CallResultData(Value.nl(new Nil()), args, Arrays.asList(args), new ComplexAction());
        }
    });

    @Before
    public void setup() throws Exception {
        startServer();

        Servlet automatonServlet = createThriftRPCService(AutomatonSrv.Iface.class, new AutomatonMockSrvImpl(buildURI(processorPath)));
        Map<String, Servlet> servlets = new HashMap<>();
        servlets.put(automatonPath, automatonServlet);
        servlets.put(processorPath, processorServlet);
        addServlets(servlets);

        thriftClient = new THSpawnClientBuilder()
                .withAddress(buildURI(automatonPath))
                .withNetworkTimeout(0)
                .build(AutomatonSrv.Iface.class);
        aClient = new TBaseAutomatonClient<>(thriftClient, "machinarium", Value.class);
    }

    @Test(expected = NamespaceNotFoundException.class)
    public void testNamespaceNotFound() {
        new TBaseAutomatonClient<>(thriftClient, "not_found", Value.class)
                .start("kek", Value.b(true));
    }

    @Test
    public void testStartMachine() {
        String machineId = "start_test";
        aClient.start(machineId, Value.b(true));
        try {
            aClient.start(machineId, Value.b(false));
            fail();
        } catch (MachineAlreadyExistsException ex) {

        }
        List<TMachineEvent<Value>> events = aClient.getEvents(machineId, new HistoryRange());
        assertEquals(1, events.size());
        assertEquals(Value.b(true), events.get(0).getData());
    }

    @Test
    public void testStartAndCallMachine() {
        String machineId = "call_test";
        aClient.start(machineId, Value.b(true));
        Value value = aClient.call(machineId, Value.b(false));
        assertEquals(Value.b(false), value);
        List<TMachineEvent<Value>> events = aClient.getEvents(machineId, new HistoryRange());
        assertEquals(2, events.size());
        assertEquals(Value.b(true), events.get(0).getData());
        assertEquals(Value.b(false), events.get(1).getData());
    }


}
