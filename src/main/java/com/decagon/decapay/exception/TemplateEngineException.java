package com.decagon.decapay.exception;

import static com.decagon.decapay.constants.ResponseMessageConstants.UNABLE_TO_PROCESS_TEMPLATE;

public class TemplateEngineException extends GenericRuntimeException {
    private static final String ERROR_CODE = "400";

    public TemplateEngineException() {
        super(UNABLE_TO_PROCESS_TEMPLATE);
    }

    public TemplateEngineException(String errorCode, String message) {
        super(errorCode,message);
    }

    public TemplateEngineException(String message) {
        super(ERROR_CODE,message);
    }

    public TemplateEngineException(String message, Throwable e) {
        super(message,e);
        this.setErrorCode(ERROR_CODE);
    }
}
