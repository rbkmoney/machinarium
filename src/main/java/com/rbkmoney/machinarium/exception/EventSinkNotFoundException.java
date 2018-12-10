package com.rbkmoney.machinarium.exception;

public class EventSinkNotFoundException extends RuntimeException {

    private final String eventSinkId;

    public EventSinkNotFoundException(String eventSinkId) {
        this.eventSinkId = eventSinkId;
    }

    public EventSinkNotFoundException(String message, String eventSinkId) {
        super(message);
        this.eventSinkId = eventSinkId;
    }

    public EventSinkNotFoundException(String message, Throwable cause, String eventSinkId) {
        super(message, cause);
        this.eventSinkId = eventSinkId;
    }

    public EventSinkNotFoundException(Throwable cause, String eventSinkId) {
        super(cause);
        this.eventSinkId = eventSinkId;
    }

    public EventSinkNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String eventSinkId) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.eventSinkId = eventSinkId;
    }

    public String getEventSinkId() {
        return eventSinkId;
    }

    @Override
    public String toString() {
        String msg = super.toString();
        return "EventSinkNotFoundException{" +
                "eventSinkId='" + eventSinkId + '\'' +
                ", " + msg +
                '}';
    }
}
