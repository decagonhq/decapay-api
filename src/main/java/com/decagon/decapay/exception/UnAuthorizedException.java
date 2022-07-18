package com.decagon.decapay.exception;

public class UnAuthorizedException extends RuntimeException {

  private static final String ERROR_CODE = "401";

  public UnAuthorizedException() {
    super("Not authorized");
  }

  public UnAuthorizedException(String errorCode, String message) {
    super(errorCode);
  }

  public UnAuthorizedException(String message) {
    super(message);
  }

  public UnAuthorizedException(String message, Throwable e) {
    super(message);
  }
}
