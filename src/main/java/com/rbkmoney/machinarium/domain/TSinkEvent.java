package com.rbkmoney.machinarium.domain;

public class TSinkEvent<T> {

    private final long id;

    private final String namespace;

    private final String sourceId;

    private final TMachineEvent<T> event;

    public TSinkEvent(long id, String namespace, String sourceId, TMachineEvent<T> event) {
        this.id = id;
        this.namespace = namespace;
        this.sourceId = sourceId;
        this.event = event;
    }

    public long getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getSourceId() {
        return sourceId;
    }

    public TMachineEvent<T> getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "TSinkEvent{" +
                "id=" + id +
                ", namespace='" + namespace + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", event=" + event +
                '}';
    }
}
