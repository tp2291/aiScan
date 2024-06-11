package com.cisco.wxcc.saa.abo.exceptions;

public class IntegrationException extends Exception {

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(Throwable throwable) {
        super(throwable);
    }

    public IntegrationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
