package com.decagon.decapay.exception;

import com.decagon.decapay.validator.ValidationErrors;
import org.springframework.validation.Errors;

public class InvalidRequestException extends RuntimeException {

	private Errors bindingResult;
	private ValidationErrors validationErrors;

	public InvalidRequestException(Errors bindingResult) {
		super("Invalid Request");
		this.bindingResult = bindingResult;
	}

    public InvalidRequestException(String message) {
		super(message);
    }

    public Errors getBindingResult() {
		return bindingResult;
	}

	public InvalidRequestException(ValidationErrors validationErrors) {
		super("Invalid Request");
		this.validationErrors = validationErrors;
	}

	public ValidationErrors getValidationErrors() {
		return validationErrors;
	}
}
