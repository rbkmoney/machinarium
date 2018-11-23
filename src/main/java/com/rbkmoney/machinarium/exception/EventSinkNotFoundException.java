package com.rbkmoney.machinarium.exception;

public class EventSinkNotFoundException extends RuntimeException {

    public EventSinkNotFoundException() {
    }

    public EventSinkNotFoundException(String message) {
        super(message);
    }

    public EventSinkNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventSinkNotFoundException(Throwable cause) {
        super(cause);
    }

    public EventSinkNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
