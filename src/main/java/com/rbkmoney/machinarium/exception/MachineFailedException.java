package com.rbkmoney.machinarium.exception;

public class MachineFailedException extends RuntimeException {

    public MachineFailedException() {
    }

    public MachineFailedException(String message) {
        super(message);
    }

    public MachineFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineFailedException(Throwable cause) {
        super(cause);
    }

    public MachineFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
