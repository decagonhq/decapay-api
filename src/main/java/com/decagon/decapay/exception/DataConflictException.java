package com.decagon.decapay.exception;


public class DataConflictException extends GenericRuntimeException {

  private static final String ERROR_CODE = "409";

  public DataConflictException() {
    super("Data Conflict");
  }

  public DataConflictException(String errorCode, String message) {
    super(errorCode,message);
  }

  public DataConflictException(String message) {
    super(ERROR_CODE,message);
  }

  public DataConflictException(String message, Throwable e) {
    super(message,e);
    this.setErrorCode(ERROR_CODE);
  }

}
