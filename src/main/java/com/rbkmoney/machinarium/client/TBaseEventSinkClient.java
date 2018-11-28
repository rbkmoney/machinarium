package com.rbkmoney.machinarium.client;

import com.rbkmoney.machinarium.domain.TSinkEvent;
import com.rbkmoney.machinarium.exception.EventSinkNotFoundException;
import com.rbkmoney.machinarium.util.TMachineUtil;
import com.rbkmoney.machinegun.stateproc.EventSinkNotFound;
import com.rbkmoney.machinegun.stateproc.EventSinkSrv;
import com.rbkmoney.machinegun.stateproc.HistoryRange;
import com.rbkmoney.machinegun.stateproc.SinkEvent;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TBaseEventSinkClient<T extends TBase> implements EventSinkClient<T> {

    private final EventSinkSrv.Iface client;

    private final String eventSinkId;

    private final Class<T> eventType;

    public TBaseEventSinkClient(EventSinkSrv.Iface client, String eventSinkId, Class<T> eventType) {
        this.client = client;
        this.eventSinkId = eventSinkId;
        this.eventType = eventType;
    }

    @Override
    public List<TSinkEvent<T>> getEvents(int limit) {
        HistoryRange historyRange = new HistoryRange();
        historyRange.setLimit(limit);

        return getEvents(historyRange);
    }

    @Override
    public List<TSinkEvent<T>> getEvents(int limit, long after) {
        HistoryRange historyRange = new HistoryRange();
        historyRange.setAfter(after);
        historyRange.setLimit(limit);

        return getEvents(historyRange);
    }

    private List<TSinkEvent<T>> getEvents(HistoryRange historyRange) {
        try {
            return client.getHistory(eventSinkId, historyRange).stream()
                    .sorted(Comparator.comparingLong(SinkEvent::getId))
                    .map(this::buildTSinkEvent)
                    .collect(Collectors.toList());
        } catch (EventSinkNotFound ex) {
            throw new EventSinkNotFoundException(String.format("Event sink not found, eventSinkId='%s'", eventSinkId), ex);
        } catch (TException ex) {
            throw new RuntimeException(ex);
        }
    }

    private TSinkEvent<T> buildTSinkEvent(SinkEvent sinkEvent) {
        return buildTSinkEvent(sinkEvent, eventType);
    }

    private TSinkEvent<T> buildTSinkEvent(SinkEvent sinkEvent, Class<T> eventType) {
        return new TSinkEvent<>(
                sinkEvent.getId(),
                sinkEvent.getSourceNs(),
                sinkEvent.getSourceId(),
                TMachineUtil.eventToTMachineEvent(sinkEvent.getEvent(), eventType)
        );
    }

}
