package com.decagon.decapay.exception;

import com.decagon.decapay.enumTypes.EntityType;
import com.decagon.decapay.enumTypes.ExceptionType;
import com.decagon.decapay.util.ErrorMsgUtils;

public class ResourceNotFoundException extends RuntimeException {

    private static final String ERROR_CODE = "404";

    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(EntityType entityType, String id) {
      super(ErrorMsgUtils.formatMsg(entityType.name(), ExceptionType.ENTITY_NOT_FOUND.getValue(),id));
    }
}
