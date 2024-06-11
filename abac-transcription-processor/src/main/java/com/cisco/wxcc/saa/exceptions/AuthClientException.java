package com.cisco.wxcc.saa.exceptions;


public class AuthClientException extends Exception {

  public AuthClientException() {
    super();
  }

  public AuthClientException(String message) {
    super(message);
  }

  public AuthClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthClientException(Throwable cause) {
    super(cause);
  }

  public AuthClientException(String message, Throwable cause, boolean enableSuppression,
                             boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
