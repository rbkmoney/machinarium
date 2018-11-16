package com.rbkmoney.machinarium.client;

import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.machinarium.domain.TMachineEvent;
import com.rbkmoney.machinarium.exception.*;
import com.rbkmoney.machinarium.util.MachineUtil;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.*;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.util.List;

public class TBaseAutomatonClient<A extends TBase, V extends TBase> implements AutomatonClient<A, V> {

    private final AutomatonSrv.Iface client;

    private final String namespace;

    private final Class<V> resultType;

    public TBaseAutomatonClient(AutomatonSrv.Iface client, String namespace, Class<V> resultType) {
        this.client = client;
        this.namespace = namespace;
        this.resultType = resultType;
    }

    @Override
    public void start(String machineId, A args) throws MachineAlreadyExistsException, MachineFailedException, NamespaceNotFoundException {
        try {
            client.start(namespace, machineId, Value.bin(Geck.toMsgPack(args)));
        } catch (MachineFailed ex) {
            throw new MachineFailedException(String.format("Machine failed, namespace='%s', machineId='%s', args='%s'", namespace, machineId, args), ex);
        } catch (MachineAlreadyExists ex) {
            throw new MachineAlreadyExistsException(String.format("Machine already exists, namespace='%s', machineId='%s'", namespace, machineId), ex);
        } catch (NamespaceNotFound ex) {
            throw new NamespaceNotFoundException(String.format("Namespace not found, namespace='%s'", namespace), ex);
        } catch (TException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public V call(String machineId, A args) throws NamespaceNotFoundException, MachineFailedException, MachineNotFoundException, MachineAlreadyWorkingException {
        try {
            Value value = client.call(new MachineDescriptor(namespace, Reference.id(machineId), new HistoryRange()), Value.bin(Geck.toMsgPack(args)));
            return Geck.msgPackToTBase(value.getBin(), resultType);
        } catch (MachineFailed ex) {
            throw new MachineFailedException(String.format("Machine failed, namespace='%s', machineId='%s', args='%s'", namespace, machineId, args), ex);
        } catch (NamespaceNotFound ex) {
            throw new NamespaceNotFoundException(String.format("Namespace not found, namespace='%s'", namespace), ex);
        } catch (MachineNotFound ex) {
            throw new MachineNotFoundException(String.format("Machine not found, namespace='%s', machineId='%s'", namespace, machineId), ex);
        } catch (MachineAlreadyWorking ex) {
            throw new MachineAlreadyWorkingException(String.format("Machine already working, namespace='%s', machineId='%s'", namespace, machineId), ex);
        } catch (TException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Machine getMachine(String machineId) throws MachineNotFoundException, NamespaceNotFoundException {
        try {
            return client.getMachine(new MachineDescriptor(namespace, Reference.id(machineId), new HistoryRange()));
        } catch (MachineNotFound ex) {
            throw new MachineNotFoundException(String.format("Machine not found, namespace='%s', machineId='%s'", namespace, machineId), ex);
        } catch (NamespaceNotFound ex) {
            throw new NamespaceNotFoundException(String.format("Namespace not found, namespace='%s'", namespace), ex);
        } catch (TException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<TMachineEvent<V>> getEvents(String machineId) throws MachineNotFoundException, NamespaceNotFoundException {
        Machine machine = getMachine(machineId);
        return MachineUtil.getMachineEvents(machine, resultType);
    }

}
