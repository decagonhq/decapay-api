package com.decagon.decapay.exception;

public class AccountDisabledException extends GenericRuntimeException {

  private static final String ERROR_CODE = "403";

  public AccountDisabledException() {
    super("Account disabled");
  }

  public AccountDisabledException(String errorCode, String message) {
    super(errorCode,message);
  }

  public AccountDisabledException(String message) {
    super(ERROR_CODE,message);
  }

  public AccountDisabledException(String message, Throwable e) {
    super(message,e);
    this.setErrorCode(ERROR_CODE);
  }
}
