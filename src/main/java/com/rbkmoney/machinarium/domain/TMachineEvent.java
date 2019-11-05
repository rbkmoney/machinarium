package com.rbkmoney.machinarium.domain;

import lombok.Data;

import java.time.Instant;

@Data
public class TMachineEvent<T> {

    private final long id;

    private final Instant createdAt;

    private final T data;

}
