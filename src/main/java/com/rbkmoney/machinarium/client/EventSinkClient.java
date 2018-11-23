package com.rbkmoney.machinarium.client;

import com.rbkmoney.machinarium.domain.TSinkEvent;

import java.util.List;
import java.util.Optional;

public interface EventSinkClient<T> {

    List<TSinkEvent<T>> getEvents(Optional<Long> after, int limit);

}
