package com.rbkmoney.machinarium.mapper;

import com.rbkmoney.machinarium.domain.TMachineEvent;
import com.rbkmoney.machinegun.stateproc.Event;

import java.util.function.Function;

public class TEventMapper<T> implements Function<Event, TMachineEvent<T>> {

    @Override
    public TMachineEvent<T> apply(Event event) {
        return null;
    }
}
