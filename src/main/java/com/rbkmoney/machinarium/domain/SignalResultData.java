package com.rbkmoney.machinarium.domain;

import com.rbkmoney.machinegun.stateproc.ComplexAction;

import java.util.List;

public class SignalResultData<T> {

    private final List<T> newEvents;

    private final ComplexAction complexAction;

    public SignalResultData(List<T> newEvents, ComplexAction complexAction) {
        this.newEvents = newEvents;
        this.complexAction = complexAction;
    }

    public List<T> getNewEvents() {
        return newEvents;
    }

    public ComplexAction getComplexAction() {
        return complexAction;
    }

    @Override
    public String toString() {
        return "SignalResultData{" +
                "newEvents=" + newEvents +
                ", complexAction=" + complexAction +
                '}';
    }
}
