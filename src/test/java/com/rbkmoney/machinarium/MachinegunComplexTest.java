package com.rbkmoney.machinarium;

import com.rbkmoney.machinarium.client.AutomatonClient;
import com.rbkmoney.machinarium.client.TBaseAutomatonClient;
import com.rbkmoney.machinarium.domain.CallResultData;
import com.rbkmoney.machinarium.domain.SignalResultData;
import com.rbkmoney.machinarium.domain.TMachineEvent;
import com.rbkmoney.machinarium.exception.MachineAlreadyExistsException;
import com.rbkmoney.machinarium.exception.NamespaceNotFoundException;
import com.rbkmoney.machinarium.handler.AbstractProcessorHandler;
import com.rbkmoney.machinegun.base.Timer;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.*;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.MountableFile;

import javax.servlet.Servlet;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class MachinegunComplexTest {

    public final static String MG_IMAGE = "dr.rbkmoney.com/rbkmoney/machinegun";
    public final static String MG_TAG = "05100794c4432601d22e50754d17312e70597696";

    private AutomatonClient<Value, Value> aClient;
    private AutomatonSrv.Iface thriftClient;

    private HandlerCollection handlerCollection;
    private Server server;
    private int serverPort = 8080;


    private Servlet testServlet = createThriftRPCService(ProcessorSrv.Iface.class, new AbstractProcessorHandler<Value, Value>(Value.class, Value.class) {

        @Override
        protected SignalResultData<Value> processSignalInit(String namespace, String machineId, Value args) {
            return new SignalResultData(Arrays.asList(args), new ComplexAction());
        }

        @Override
        protected SignalResultData<Value> processSignalTimeout(String namespace, String machineId, List<TMachineEvent<Value>> tMachineEvents) {
            return new SignalResultData(Arrays.asList(Value.str("timeout")), new ComplexAction());
        }

        @Override
        protected CallResultData<Value> processCall(String namespace, String machineId, Value args, List<TMachineEvent<Value>> tMachineEvents) {
            return new CallResultData(args, Arrays.asList(args), new ComplexAction());
        }
    });

    @ClassRule
    public static GenericContainer machinegunContainer = new GenericContainer(MG_IMAGE + ":" + MG_TAG)
            .withExposedPorts(8022)
            .withClasspathResourceMapping(
                    "/machinegun/config.yaml",
                    "/opt/machinegun/etc/config.yaml",
                    BindMode.READ_ONLY
            ).waitingFor(
                    new HttpWaitStrategy()
                            .forPath("/health")
                            .forStatusCode(200)
            );

    @Before
    public void setup() throws Exception {
        thriftClient = new THSpawnClientBuilder()
                .withAddress(new URI("http://localhost:" + machinegunContainer.getMappedPort(8022) + "/v1/automaton"))
                .withNetworkTimeout(0)
                .build(AutomatonSrv.Iface.class);
        aClient = new TBaseAutomatonClient<>(thriftClient, "machinarium", Value.class);

        server = new Server(serverPort);
        HandlerCollection contextHandlerCollection = new HandlerCollection(true);
        this.handlerCollection = contextHandlerCollection;
        server.setHandler(contextHandlerCollection);

        server.start();
        addServlet(testServlet, "/v1/processor");
    }

    protected void addServlet(Servlet servlet, String mapping) {
        try {
            ServletContextHandler context = new ServletContextHandler();
            ServletHolder defaultServ = new ServletHolder(mapping, servlet);
            context.addServlet(defaultServ, mapping);
            handlerCollection.addHandler(context);
            context.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> Servlet createThriftRPCService(Class<T> iface, T handler) {
        THServiceBuilder serviceBuilder = new THServiceBuilder();
        return serviceBuilder.build(iface, handler);
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
        List<TMachineEvent<Value>> events = aClient.getEvents(machineId);
        assertEquals(1, events.size());
        assertEquals(Value.b(true), events.get(0).getData());
    }

    @Test
    public void testStartAndCallMachine() {
        String machineId = "call_test";
        aClient.start(machineId, Value.b(true));
        Value value = aClient.call(machineId, Value.b(false));
        assertEquals(Value.b(false), value);
        List<TMachineEvent<Value>> events = aClient.getEvents(machineId);
        assertEquals(2, events.size());
        assertEquals(Value.b(true), events.get(0).getData());
        assertEquals(Value.b(false), events.get(1).getData());
    }

    @After
    public void stopJetty() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
