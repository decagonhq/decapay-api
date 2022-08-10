package com.decagon.decapay.exception;

public class UnAuthorizedException extends RuntimeException{

    private final static String ERROR_CODE = "403";

    private static final long serialVersionUID = 1L;

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
