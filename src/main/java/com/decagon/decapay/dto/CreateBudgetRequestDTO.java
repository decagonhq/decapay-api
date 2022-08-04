package com.decagon.decapay.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.decagon.decapay.exception.InvalidRequestException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO containing information needed to create a budget.")
public class CreateBudgetRequestDTO {
	@NotBlank
	@Size(min = 1, max = 100, message = "Title field cannot be empty or more than 100 characters")
	@Schema(description = "Title, maximum 100 characters", required = true)
	private String title;

	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
	@Digits(integer = 10, fraction = 2, message = "Amount must be digit with at most two decimal places")
	@Schema(description = "Amount, must be digit with at most two decimal places", required = true)
	private BigDecimal amount;

	@NotBlank
	@Pattern(regexp = "ANNUAL|MONTHLY|WEEKLY|DAILY|CUSTOM")
	@Schema(description = "Period, allowable values (ANNUAL,MONTHLY,WEEKLY,DAILY,CUSTOM)", required = true)
	private String period;

	@Schema(description = "Budget start date in format yyyy-MM-dd. Required for CUSTOM period")
	private LocalDate budgetStartDate;

	@Schema(description = "Budget end date in format yyyy-MM-dd.  Required for CUSTOM period")
	private LocalDate budgetEndDate;

	@Schema(description = "budget description")
	@Size(max = 255, message = "Description should be maximum of 255 characters")
	private String description;

	public void isValidForCustomPeriod() {
		if (budgetStartDate == null || budgetEndDate == null) {
			throw new InvalidRequestException("Budget start date and end date must be provided for CUSTOM period");
		} else if (!budgetStartDate.isBefore(budgetEndDate)) {
			throw new InvalidRequestException("Budget start date must be before end date");
		}
	}
}
