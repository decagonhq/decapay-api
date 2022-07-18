package com.decagon.decapay.exception;

public class InvalidCredentialsException extends GenericRuntimeException {

  private static final String ERROR_CODE = "400";

  public InvalidCredentialsException() {
    super("Invalid credentials");
  }

  public InvalidCredentialsException(String errorCode, String message) {
    super(errorCode,message);
  }

  public InvalidCredentialsException(String message) {
    super(ERROR_CODE,message);
  }

  public InvalidCredentialsException(String message, Throwable e) {
    super(message,e);
    this.setErrorCode(ERROR_CODE);
  }
}
