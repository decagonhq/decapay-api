package com.decagon.decapay.validator.enumValidator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validates values of a String used as payload in REST service
 * Solution taken from https://funofprograming.wordpress.com/2016/09/29/java-enum-validator/
 */
public class EnumValidator implements ConstraintValidator<Enum, String> {
    private Enum annotation;

    @Override
    public void initialize(Enum annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(String valueForValidation, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = false;
        Object[] enumValues = this.annotation.enumClass().getEnumConstants();

        if(StringUtils.isEmpty(valueForValidation)&&!annotation.required()){
          return true;
        }
        if(valueForValidation==null){
            return false;
        }

        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                 if (valueForValidation.equals(enumValue.toString())
                        || (this.annotation.ignoreCase() && valueForValidation.equalsIgnoreCase(enumValue.toString()))) {
                         result = true;
                    break;
                }
            }
        }
        return result;
    }
}
