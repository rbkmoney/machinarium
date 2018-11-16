package com.rbkmoney.machinarium.exception;

public class MachineAlreadyExistsException extends RuntimeException {

    public MachineAlreadyExistsException() {
    }

    public MachineAlreadyExistsException(String message) {
        super(message);
    }

    public MachineAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public MachineAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
