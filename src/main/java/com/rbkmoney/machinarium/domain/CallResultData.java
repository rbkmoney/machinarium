package com.rbkmoney.machinarium.domain;

import com.rbkmoney.machinegun.stateproc.ComplexAction;

import java.util.List;

public class CallResultData<T> {

    private final T callResult;

    private final List<T> newEvents;

    private final ComplexAction complexAction;

    public CallResultData(T callResult, List<T> newEvents, ComplexAction complexAction) {
        this.callResult = callResult;
        this.newEvents = newEvents;
        this.complexAction = complexAction;
    }

    public T getCallResult() {
        return callResult;
    }

    public List<T> getNewEvents() {
        return newEvents;
    }

    public ComplexAction getComplexAction() {
        return complexAction;
    }

    @Override
    public String toString() {
        return "CallResultData{" +
                "callResult=" + callResult +
                ", newEvents=" + newEvents +
                ", complexAction=" + complexAction +
                '}';
    }
}
