package com.cisco.wxcc.saa.exceptions;

public class ConfigurationException extends Exception {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(Throwable throwable) {
        super(throwable);
    }

    public ConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}