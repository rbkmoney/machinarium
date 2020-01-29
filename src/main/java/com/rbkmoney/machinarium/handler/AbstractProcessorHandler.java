package com.rbkmoney.machinarium.handler;

import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.machinarium.domain.CallResultData;
import com.rbkmoney.machinarium.domain.SignalResultData;
import com.rbkmoney.machinarium.domain.TMachine;
import com.rbkmoney.machinarium.domain.TMachineEvent;
import com.rbkmoney.machinarium.util.TMachineUtil;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.*;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractProcessorHandler<A extends TBase, V extends TBase> implements ProcessorSrv.Iface {

    private final Class<A> argsType;

    private final Class<V> resultType;

    public AbstractProcessorHandler(Class<A> argsType, Class<V> resultType) {
        this.argsType = argsType;
        this.resultType = resultType;
    }

    @Override
    public SignalResult processSignal(SignalArgs args) throws TException {
        Signal._Fields signalType = args.getSignal().getSetField();
        Machine machine = args.getMachine();
        SignalResultData<V> signalResult = processSignal(signalType, args, machine);

        return new SignalResult(
                buildMachineStateChange(signalResult.getState(), signalResult.getNewEvents()),
                signalResult.getComplexAction()
        );
    }

    @Override
    public CallResult processCall(CallArgs args) throws TException {
        Machine machine = args.getMachine();
        CallResultData<V> callResult = processCall(
                machine.getNs(),
                machine.getId(),
                Geck.msgPackToTBase(args.getArg().getBin(), argsType),
                TMachineUtil.getMachineEvents(machine, resultType)
        );

        return new CallResult(
                Value.bin(Geck.toMsgPack(callResult.getCallResult())),
                buildMachineStateChange(callResult.getState(), callResult.getNewEvents()),
                callResult.getComplexAction()
        );
    }

    private SignalResultData<V> processSignal(Signal._Fields signalType, SignalArgs args, Machine machine) {
        TMachine tMachine = new TMachine(machine.getNs(), machine.getId(), machine.getTimer(), machine.getAuxState());
        switch (signalType) {
            case INIT:
                InitSignal initSignal = args.getSignal().getInit();
                return processSignalInit(tMachine, Geck.msgPackToTBase(initSignal.getArg().getBin(), argsType));
            case TIMEOUT:
                return processSignalTimeout(tMachine, TMachineUtil.getMachineEvents(machine, resultType));
            default:
                throw new UnsupportedOperationException(String.format("Unsupported signal type, signalType='%s'", signalType));
        }
    }

    @Override
    public RepairResult processRepair(RepairArgs repairArgs) throws RepairFailed, TException {
        throw new UnsupportedOperationException("processRepair not implemented");
    }

    private MachineStateChange buildMachineStateChange(Value state, List<V> newEvents) {
        MachineStateChange machineStateChange = new MachineStateChange();
        machineStateChange.setAuxState(new Content(state));
        List<Content> contentList = newEvents.stream()
                .map(event -> new Content(Value.bin(Geck.toMsgPack(event))))
                .collect(Collectors.toList());
        machineStateChange.setEvents(contentList);
        return machineStateChange;
    }

    protected abstract SignalResultData<V> processSignalInit(TMachine machine, A args);

    protected abstract SignalResultData<V> processSignalTimeout(TMachine machine, List<TMachineEvent<V>> events);

    protected abstract CallResultData<V> processCall(String namespace, String machineId, A args, List<TMachineEvent<V>> events);

}
