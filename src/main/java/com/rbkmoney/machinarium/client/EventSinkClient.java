package com.rbkmoney.machinarium.client;

import com.rbkmoney.machinarium.domain.TSinkEvent;
import com.rbkmoney.machinarium.exception.EventSinkNotFoundException;

import java.util.List;
import java.util.Optional;

public interface EventSinkClient<T> {

    List<TSinkEvent<T>> getEvents(int limit) throws EventSinkNotFoundException;

    List<TSinkEvent<T>> getEvents(int limit, long after) throws EventSinkNotFoundException;

    Optional<Long> getLastEventId() throws EventSinkNotFoundException;

}
