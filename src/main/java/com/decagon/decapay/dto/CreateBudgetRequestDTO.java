package com.decagon.decapay.dto;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.constants.SchemaConstants;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.validators.enumValidator.Enum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO containing information needed to create a budget.")
public class CreateBudgetRequestDTO {

	@NotNull(message = "Title is required")
	@Size(min = 1, max = SchemaConstants.BUDGET_TITLE_SIZE, message = "Title field cannot be empty or more than "+SchemaConstants.BUDGET_TITLE_SIZE+" characters")
	@Schema(description = "Title, maximum "+SchemaConstants.BUDGET_TITLE_SIZE+" characters", required = true)
	private String title;

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
	@Digits(integer = 10, fraction = 2, message = "Amount must be digit with at most two decimal places")
	@Schema(description = "Amount, must be digit with at most two decimal places", required = true)
	private BigDecimal amount;

	@NotBlank
	@Enum(enumClass = BudgetPeriod.class)
	@Schema(description = "Period, allowable values (ANNUAL,MONTHLY,WEEKLY,DAILY,CUSTOM)", required = true)
	private String period;

	@Schema(description = "Budget start date in format "+ DateDisplayConstants.DATE_INPUT_FORMAT +". Required for CUSTOM,DAILY,WEEKLY period")
	private String budgetStartDate;

	@Schema(description = "Budget end date in format "+DateDisplayConstants.DATE_INPUT_FORMAT+".  Required for CUSTOM,DAILY period, Same value for DAILY")
	private String budgetEndDate;

	@Schema(description = "budget description")
	@Size(max = SchemaConstants.BUDGET_DESC_SIZE, message = "Description should be maximum of "+SchemaConstants.BUDGET_DESC_SIZE+" characters")
	private String description;

	@Schema(description = "Month must be between 1 and 12. Required for MONTHLY period")
	private short month;
	@Schema(description = "Year must be 4 xters in len and a number. Required for ANNUAL,MONTHLY period")
	private short year;
	@Schema(description = "Duration must be a number. Required for WEEKLY period")
	private int duration;

}
