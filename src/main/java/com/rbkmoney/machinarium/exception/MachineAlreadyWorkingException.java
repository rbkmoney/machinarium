package com.rbkmoney.machinarium.exception;

public class MachineAlreadyWorkingException extends RuntimeException {

    public MachineAlreadyWorkingException() {
    }

    public MachineAlreadyWorkingException(String message) {
        super(message);
    }

    public MachineAlreadyWorkingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineAlreadyWorkingException(Throwable cause) {
        super(cause);
    }

    public MachineAlreadyWorkingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
