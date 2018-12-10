package com.rbkmoney.machinarium;

import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.machinarium.client.TBaseEventSinkClient;
import com.rbkmoney.machinarium.domain.TSinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.*;
import org.apache.thrift.TException;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TBaseEventSinkClientTest {

    EventSinkSrv.Iface client;

    HistoryRange range;

    @Test
    public void testGetEventsWithoutAfter() throws TException {
        client = mock(EventSinkSrv.Iface.class);
        range = new HistoryRange();
        range.setLimit(10);

        when(client.getHistory(any(), eq(range)))
                .thenReturn(buildSinkEvents());

        TBaseEventSinkClient<Value> tBaseEventSinkClient = new TBaseEventSinkClient<>(client, "test", Value.class);
        List<TSinkEvent<Value>> events = tBaseEventSinkClient.getEvents(range.getLimit());
        assertFalse(events.isEmpty());
        assertEquals(Value.b(true), events.get(0).getEvent().getData());
    }

    @Test
    public void testGetEventsWithAfter() throws TException {
        client = mock(EventSinkSrv.Iface.class);
        range = new HistoryRange();
        range.setAfter(1);
        range.setLimit(10);

        when(client.getHistory(any(), eq(range)))
                .thenReturn(buildSinkEvents());

        TBaseEventSinkClient<Value> tBaseEventSinkClient = new TBaseEventSinkClient<>(client, "test", Value.class);
        List<TSinkEvent<Value>> events = tBaseEventSinkClient.getEvents(range.getLimit(), 1L);
        assertFalse(events.isEmpty());
        assertEquals(Value.b(true), events.get(0).getEvent().getData());
    }

    @Test
    public void testGetLastEventId() throws TException {
        client = mock(EventSinkSrv.Iface.class);
        range = new HistoryRange();
        range.setDirection(Direction.backward);
        range.setLimit(1);

        when(client.getHistory(any(), eq(range)))
                .thenReturn(buildSinkEvents());

        TBaseEventSinkClient<Value> tBaseEventSinkClient = new TBaseEventSinkClient<>(client, "test", Value.class);
        Optional<Long> lastEventId = tBaseEventSinkClient.getLastEventId();
        assertTrue(lastEventId.isPresent());
        assertEquals(buildSinkEvents().get(0).getEvent().getId(), (long) lastEventId.get());
    }

    @Test
    public void testWhenLastEventIdIsEmpty() throws TException {
        client = mock(EventSinkSrv.Iface.class);
        range = new HistoryRange();
        range.setDirection(Direction.backward);
        range.setLimit(1);

        when(client.getHistory(any(), eq(range)))
                .thenReturn(Collections.emptyList());

        TBaseEventSinkClient<Value> tBaseEventSinkClient = new TBaseEventSinkClient<>(client, "test", Value.class);
        Optional<Long> lastEventId = tBaseEventSinkClient.getLastEventId();
        assertFalse(lastEventId.isPresent());
    }

    private List<SinkEvent> buildSinkEvents() {
        return Arrays.asList(
                new SinkEvent(
                        1L,
                        "source_id",
                        "source_namespace",
                        new Event(
                                1L,
                                Instant.now().toString(),
                                Value.bin(Geck.toMsgPack(Value.b(true)))
                        )

                )
        );
    }


}
