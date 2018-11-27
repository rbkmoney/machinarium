package com.rbkmoney.machinarium.client;

import com.rbkmoney.machinarium.domain.TSinkEvent;

import java.util.List;

public interface EventSinkClient<T> {

    List<TSinkEvent<T>> getEvents(int limit);

    List<TSinkEvent<T>> getEvents(int limit, long after);

}
