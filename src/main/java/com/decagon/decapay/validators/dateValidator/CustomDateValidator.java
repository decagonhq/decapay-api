package com.decagon.decapay.validators.dateValidator;



import com.decagon.decapay.utils.CustomDateUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomDateValidator implements ConstraintValidator<CustomDate, String> {
	private String format;
	private boolean required;

	@Override
	public void initialize(CustomDate customDate) {
		format = customDate.format();
		required = customDate.required();
	}

	@Override
	public boolean isValid(String date, ConstraintValidatorContext context) {
		if (date == null || date.isEmpty()) {
			return !required;
		}
		return CustomDateUtil.isValidFormat(format,date);
	}


}
