package com.rbkmoney.machinarium.client;

import com.rbkmoney.machinarium.domain.TMachineEvent;
import com.rbkmoney.machinarium.exception.*;
import com.rbkmoney.machinegun.stateproc.Machine;

import java.util.List;

public interface AutomatonClient<A, V> {

    void start(String machineId, A args) throws MachineAlreadyExistsException, MachineFailedException, NamespaceNotFoundException;

    V call(String machineId, A args) throws NamespaceNotFoundException, MachineFailedException, MachineNotFoundException, MachineAlreadyWorkingException;

    Machine getMachine(String machineId) throws MachineNotFoundException, NamespaceNotFoundException;

    List<TMachineEvent<V>> getEvents(String machineId) throws MachineNotFoundException, NamespaceNotFoundException;

}
