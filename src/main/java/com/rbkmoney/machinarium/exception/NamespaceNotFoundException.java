package com.rbkmoney.machinarium.exception;

public class NamespaceNotFoundException extends RuntimeException {

    public NamespaceNotFoundException() {
    }

    public NamespaceNotFoundException(String message) {
        super(message);
    }

    public NamespaceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NamespaceNotFoundException(Throwable cause) {
        super(cause);
    }

    public NamespaceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
