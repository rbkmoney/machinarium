package com.rbkmoney.machinarium.domain;

import com.rbkmoney.machinegun.stateproc.Content;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class TMachine<E> {

    private final String ns;

    private final String machineId;

    private final Instant timer;

    private final Content machineState;

    private final List<TMachineEvent<E>> machineEvent;

}
