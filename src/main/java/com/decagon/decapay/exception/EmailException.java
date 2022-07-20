package com.decagon.decapay.exception;

public class EmailException extends GenericRuntimeException{

        private static final String ERROR_CODE = "500";

        public EmailException() {
            super("Email Error");
        }

        public EmailException(String errorCode, String message) {
            super(errorCode,message);
        }

        public EmailException(String message) {
            super(ERROR_CODE,message);
        }

        public EmailException(String message, Throwable e) {
            super(message,e);
            this.setErrorCode(ERROR_CODE);
        }
}
