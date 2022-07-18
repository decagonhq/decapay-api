package com.decagon.decapay.exception;

public class InvalidRefreshTokenException extends GenericRuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidRefreshTokenException(String errorCode, String message) {
        super(errorCode, message);
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }

    public InvalidRefreshTokenException(Throwable exception) {
        super(exception);
    }

    public InvalidRefreshTokenException(String message, Throwable exception) {
        super(message, exception);
    }

    public InvalidRefreshTokenException(String errorCode, String message, Throwable exception) {
        super(errorCode, message, exception);
    }
}
