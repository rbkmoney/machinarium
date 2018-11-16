package com.rbkmoney.machinarium.exception;

public class MachineNotFoundException extends RuntimeException {

    public MachineNotFoundException() {
    }

    public MachineNotFoundException(String message) {
        super(message);
    }

    public MachineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineNotFoundException(Throwable cause) {
        super(cause);
    }

    public MachineNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
