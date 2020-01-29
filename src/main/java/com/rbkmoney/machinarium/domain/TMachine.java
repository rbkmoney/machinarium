package com.rbkmoney.machinarium.domain;

import com.rbkmoney.machinegun.stateproc.Content;
import lombok.Data;

@Data
public class TMachine {

    private final String ns;

    private final String machineId;

    private final String timer;

    private final Content machineState;

}
