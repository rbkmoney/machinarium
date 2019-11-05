package com.rbkmoney.machinarium.domain;

import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.ComplexAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class SignalResultData<T> {

    private final Value state;

    private final List<T> newEvents;

    private final ComplexAction complexAction;

}
