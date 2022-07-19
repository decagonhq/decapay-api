package com.decagon.decapay.exception;


public class ResourceConflictException extends GenericRuntimeException {

  private static final String ERROR_CODE = "409";

  public ResourceConflictException() {
    super("Data Conflict");
  }

  public ResourceConflictException(String errorCode, String message) {
    super(errorCode,message);
  }

  public ResourceConflictException(String message) {
    super(ERROR_CODE,message);
  }

  public ResourceConflictException(String message, Throwable e) {
    super(message,e);
    this.setErrorCode(ERROR_CODE);
  }

}
