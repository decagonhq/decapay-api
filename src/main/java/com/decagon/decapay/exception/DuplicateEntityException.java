package com.decagon.decapay.exception;

import com.decagon.decapay.enumTypes.EntityType;
import com.decagon.decapay.enumTypes.ExceptionType;
import com.decagon.decapay.util.ErrorMsgUtils;

public class DuplicateEntityException extends RuntimeException {

  private static final String ERROR_CODE = "405";

  private static final long serialVersionUID = 1L;

    public DuplicateEntityException(String errorCode, String message) {
        super(errorCode);
    }

    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(EntityType entityType, String id) {
      super(ErrorMsgUtils.formatMsg(entityType.name(), ExceptionType.DUPLICATE_ENTITY.getValue(),id));
    }
}
