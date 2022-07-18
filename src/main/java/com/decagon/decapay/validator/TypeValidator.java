package com.decagon.decapay.validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class TypeValidator<T> {
    private T item;

    public TypeValidator(T type) {
        item=type;
    }

    public Set<ConstraintViolation<T>> validate() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(item);
    }

    public void setItem(T item) {
        this.item = item;
    }
}
