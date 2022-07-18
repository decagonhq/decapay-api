package com.decagon.decapay.exception;

import com.decagon.decapay.enumTypes.EntityType;

public class UnAuthorizeEntityAccessException extends RuntimeException {
    public UnAuthorizeEntityAccessException(String message) {
        super(message);
    }

    public UnAuthorizeEntityAccessException(EntityType country, String message) {
    }

    public UnAuthorizeEntityAccessException(EntityType entityType) {
        super("Access denied: Trying to access resource:"+entityType+" which does not belong to you");
    }

    public UnAuthorizeEntityAccessException() {
        super("Unauthorised resource");
    }
}
