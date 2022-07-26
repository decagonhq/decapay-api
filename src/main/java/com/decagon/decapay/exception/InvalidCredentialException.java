package com.decagon.decapay.exception;


public class InvalidCredentialException extends GenericRuntimeException {

	private static final String ERROR_CODE = "400";

	public InvalidCredentialException() {
		super("Invalid Request");
	}

	public InvalidCredentialException(String errorCode, String message) {
		super(errorCode,message);
	}

	public InvalidCredentialException(String message) {
		super(ERROR_CODE,message);
	}

	public InvalidCredentialException(String message, Throwable e) {
		super(message,e);
		this.setErrorCode(ERROR_CODE);
	}
}
