package com.rbkmoney.machinarium.handler;

import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.machinarium.domain.CallResultData;
import com.rbkmoney.machinarium.domain.SignalResultData;
import com.rbkmoney.machinarium.domain.TMachineEvent;
import com.rbkmoney.machinarium.util.MachineUtil;
import com.rbkmoney.machinegun.msgpack.Nil;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.*;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractProcessorHandler<T extends TBase> implements ProcessorSrv.Iface {

    private final Class<T> resultType;

    public AbstractProcessorHandler(Class<T> resultType) {
        this.resultType = resultType;
    }

    @Override
    public SignalResult processSignal(SignalArgs args) throws TException {
        Signal._Fields signalType = args.getSignal().getSetField();
        Machine machine = args.getMachine();
        SignalResultData signalResult = processSignal(signalType, args, machine);

        return new SignalResult(
                buildMachineStateChange(signalResult.getNewEvents()),
                signalResult.getComplexAction()
        );
    }

    @Override
    public CallResult processCall(CallArgs args) throws TException {
        Machine machine = args.getMachine();
        CallResultData callResult = processCall(machine.getNs(), machine.getId(), args.getArg(), MachineUtil.getMachineEvents(machine, resultType));
        List<T> newEvents = callResult.getNewEvents();

        return new CallResult(
                Value.bin(Geck.toMsgPack(callResult.getCallResult())),
                buildMachineStateChange(newEvents),
                callResult.getComplexAction()
        );
    }

    private SignalResultData processSignal(Signal._Fields signalType, SignalArgs args, Machine machine) {
        switch (signalType) {
            case INIT:
                InitSignal initSignal = args.getSignal().getInit();
                return processSignalInit(machine.getNs(), machine.getId(), initSignal.getArg());
            case TIMEOUT:
                return processSignalTimeout(machine.getNs(), machine.getId(), MachineUtil.getMachineEvents(machine, resultType));
            default:
                throw new UnsupportedOperationException(String.format("Unsupported signal type, signalType='%s'", signalType));
        }
    }

    private MachineStateChange buildMachineStateChange(List<T> newEvents) {
        MachineStateChange machineStateChange = new MachineStateChange();
        machineStateChange.setAuxStateLegacy(Value.nl(new Nil())); //??
        machineStateChange.setEventsLegacy(
                newEvents.stream()
                        .map(event -> Value.bin(Geck.toMsgPack(event)))
                        .collect(Collectors.toList())
        );
        return machineStateChange;
    }

    protected abstract SignalResultData processSignalInit(String namespace, String machineId, Value args);

    protected abstract SignalResultData processSignalTimeout(String namespace, String machineId, List<TMachineEvent<T>> events);

    protected abstract CallResultData processCall(String namespace, String machineId, Value args, List<TMachineEvent<T>> events);

}
