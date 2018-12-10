package com.rbkmoney.machinarium.client;

import com.rbkmoney.machinarium.domain.TSinkEvent;
import com.rbkmoney.machinarium.exception.EventSinkNotFoundException;
import com.rbkmoney.machinarium.util.TMachineUtil;
import com.rbkmoney.machinegun.stateproc.*;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    public List<TSinkEvent<T>> getEvents(int limit, long after) throws EventSinkNotFoundException {
        HistoryRange historyRange = new HistoryRange();
        historyRange.setAfter(after);
        historyRange.setLimit(limit);

        return getEvents(historyRange);
    }

    @Override
    public Optional<Long> getLastEventId() throws EventSinkNotFoundException {
        HistoryRange historyRange = new HistoryRange();
        historyRange.setDirection(Direction.backward);
        historyRange.setLimit(1);

        try {
            return client.getHistory(eventSinkId, historyRange).stream()
                    .findFirst().map(event -> event.getEvent().getId());
        } catch (EventSinkNotFound ex) {
            throw new EventSinkNotFoundException(ex, eventSinkId);
        } catch (TException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<TSinkEvent<T>> getEvents(HistoryRange historyRange) throws EventSinkNotFoundException {
        try {
            return client.getHistory(eventSinkId, historyRange).stream()
                    .sorted(Comparator.comparingLong(SinkEvent::getId))
                    .map(this::buildTSinkEvent)
                    .collect(Collectors.toList());
        } catch (EventSinkNotFound ex) {
            throw new EventSinkNotFoundException(ex, eventSinkId);
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
