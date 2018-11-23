package com.rbkmoney.machinarium;

import com.rbkmoney.machinarium.client.TBaseEventSinkClient;
import com.rbkmoney.machinegun.msgpack.Value;
import org.junit.Test;

public class TBaseEventSinkClientTest {

    @Test
    public void kek() {
        TBaseEventSinkClient<Value> tBaseEventSinkClient = new TBaseEventSinkClient<>(null, "", Value.class);

    }

}
