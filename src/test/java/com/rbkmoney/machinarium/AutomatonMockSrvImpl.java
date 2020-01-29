package com.rbkmoney.machinarium;

import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.*;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.thrift.TException;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class AutomatonMockSrvImpl implements AutomatonSrv.Iface {

    private final String namespace = "machinarium";

    private Map<String, Machine> machines = new HashMap<>();

    private AtomicLong eventIdCounter = new AtomicLong(1);

    private ProcessorSrv.Iface client;

    public AutomatonMockSrvImpl(URI uri) {
        client = new THSpawnClientBuilder()
                .withAddress(uri)
                .build(ProcessorSrv.Iface.class);
    }

    @Override
    public void start(String ns, String id, Value args) throws NamespaceNotFound, MachineAlreadyExists, MachineFailed, TException {
        if (!namespace.equals(ns)) {
            throw new NamespaceNotFound();
        }

        if (machines.containsKey(id)) {
            throw new MachineAlreadyExists();
        }

        Machine machine = new Machine(ns, id, new ArrayList<>(), new HistoryRange());
        SignalResult signalResult = client.processSignal(new SignalArgs(Signal.init(new InitSignal(args)), machine));
        machine.setHistory(
                signalResult.getChange().getEvents()
                        .stream().map(
                        event -> new Event(
                                eventIdCounter.getAndIncrement(),
                                Instant.now().toString(),
                                event.getData()
                        )
                ).collect(Collectors.toList())
        );
        machines.put(id, machine);
    }

    @Override
    public Value repair(MachineDescriptor desc, Value a) throws NamespaceNotFound, MachineNotFound, MachineFailed, MachineAlreadyWorking, TException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void simpleRepair(String ns, Reference ref) throws NamespaceNotFound, MachineNotFound, MachineFailed, MachineAlreadyWorking, TException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Value call(MachineDescriptor desc, Value args) throws NamespaceNotFound, MachineNotFound, MachineFailed, TException {
        if (!namespace.equals(desc.getNs())) {
            throw new NamespaceNotFound();
        }

        if (desc.getRef().isSetTag()) {
            throw new UnsupportedOperationException("Not implemented");
        }

        if (!machines.containsKey(desc.getRef().getId())) {
            throw new MachineNotFound();
        }

        Machine machine = machines.get(desc.getRef().getId());
        CallResult callResult = client.processCall(new CallArgs(args, machine));
        List<Event> events = machine.getHistory();
        events.addAll(
                callResult.getChange().getEvents()
                        .stream().map(
                        event -> new Event(
                                eventIdCounter.getAndIncrement(),
                                Instant.now().toString(),
                                event.getData()
                        )
                ).collect(Collectors.toList())
        );
        machine.setHistory(events);
        machines.put(desc.getRef().getId(), machine);

        return callResult.getResponse();
    }

    @Override
    public Machine getMachine(MachineDescriptor desc) throws NamespaceNotFound, MachineNotFound, EventNotFound, TException {
        if (!namespace.equals(desc.getNs())) {
            throw new NamespaceNotFound();
        }

        if (desc.getRef().isSetTag()) {
            throw new UnsupportedOperationException("Not implemented");
        }

        if (!machines.containsKey(desc.getRef().getId())) {
            throw new MachineNotFound();
        }

        return machines.get(desc.getRef().getId());
    }

    @Override
    public void remove(String ns, String id) throws NamespaceNotFound, MachineNotFound, TException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void modernize(MachineDescriptor desc) throws NamespaceNotFound, MachineNotFound, TException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
