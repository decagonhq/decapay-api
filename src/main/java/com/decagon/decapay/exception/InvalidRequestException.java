package com.decagon.decapay.exception;


public class InvalidRequestException extends GenericRuntimeException {

	private static final String ERROR_CODE = "400";

	public InvalidRequestException() {
		super("Invalid Request");
	}

	public InvalidRequestException(String errorCode, String message) {
		super(errorCode,message);
	}

	public InvalidRequestException(String message) {
		super(ERROR_CODE,message);
	}

	public InvalidRequestException(String message, Throwable e) {
		super(message,e);
		this.setErrorCode(ERROR_CODE);
	}
}
