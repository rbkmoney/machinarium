package com.rbkmoney.machinarium.util;

import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.machinarium.domain.TMachineEvent;
import com.rbkmoney.machinegun.stateproc.Event;
import com.rbkmoney.machinegun.stateproc.Machine;
import org.apache.thrift.TBase;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MachineUtil {

    public static <T extends TBase> List<TMachineEvent<T>> getMachineEvents(Machine machine, Class<T> eventType) {
        return machine.getHistory().stream()
                .sorted(Comparator.comparingLong(Event::getId))
                .map(
                        event -> new TMachineEvent<>(
                                event.getId(),
                                Instant.parse(event.getCreatedAt()),
                                Geck.msgPackToTBase(event.getData().getBin(), eventType)
                        )
                ).collect(Collectors.toList());
    }

}
