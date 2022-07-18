package com.decagon.decapay.exception;


public class ResourceNotFoundException extends GenericRuntimeException {

  private static final String ERROR_CODE = "404";

  public ResourceNotFoundException() {
    super("Resource not found");
  }

  public ResourceNotFoundException(String errorCode, String message) {
    super(errorCode,message);
  }

  public ResourceNotFoundException(String message) {
    super(ERROR_CODE,message);
  }

  public ResourceNotFoundException(String message, Throwable e) {
    super(message,e);
    this.setErrorCode(ERROR_CODE);
  }
}
