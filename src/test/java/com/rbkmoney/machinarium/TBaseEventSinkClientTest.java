package com.rbkmoney.machinarium;

import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.machinarium.client.TBaseEventSinkClient;
import com.rbkmoney.machinarium.domain.TSinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.machinegun.stateproc.Event;
import com.rbkmoney.machinegun.stateproc.EventSinkSrv;
import com.rbkmoney.machinegun.stateproc.HistoryRange;
import com.rbkmoney.machinegun.stateproc.SinkEvent;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TBaseEventSinkClientTest {

    EventSinkSrv.Iface client;

    HistoryRange range;

    @Before
    public void setup() throws TException {
        client = mock(EventSinkSrv.Iface.class);
        range = new HistoryRange();
        range.setLimit(10);

        when(client.getHistory(any(), eq(range)))
                .thenReturn(Arrays.asList(
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
                ));
    }

    @Test
    public void testGetEvents() {
        TBaseEventSinkClient<Value> tBaseEventSinkClient = new TBaseEventSinkClient<>(client, "test", Value.class);
        List<TSinkEvent<Value>> events = tBaseEventSinkClient.getEvents(Optional.empty(), range.getLimit());
        assertFalse(events.isEmpty());
        assertEquals(Value.b(true), events.get(0).getEvent().getData());
    }

}
