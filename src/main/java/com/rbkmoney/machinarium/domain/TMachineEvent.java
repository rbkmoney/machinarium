package com.rbkmoney.machinarium.domain;

import java.time.Instant;

public class TMachineEvent<T> {

    private final long id;

    private final Instant createdAt;

    private final T data;

    public TMachineEvent(long id, Instant createdAt, T data) {
        this.id = id;
        this.createdAt = createdAt;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "TEvent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", data=" + data +
                '}';
    }
}
