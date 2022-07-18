package com.decagon.decapay.exception;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class DecapaySystemException extends RuntimeException{

    protected String message;

    public DecapaySystemException(String message) {
        this.message=message;
    }

    public DecapaySystemException(Throwable cause) {
        super(cause);
    }

    public DecapaySystemException(String message, Throwable cause) {
        super(message, cause);
        this.message=message;
    }
}
