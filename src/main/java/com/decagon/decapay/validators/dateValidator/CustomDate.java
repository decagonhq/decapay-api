package com.decagon.decapay.validators.dateValidator;

import com.decagon.decapay.constants.DateConstants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.CONSTRUCTOR,ElementType.PARAMETER,ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy=CustomDateValidator.class)
public @interface CustomDate {
 
    String message() default "Invalid Date Format";


 Class<?>[] groups() default {};

 Class<? extends Payload>[] payload() default {};
 
 String format() default DateConstants.DATE_INPUT_FORMAT;
 boolean required() default true;
}
