package com.decagon.decapay.payloads.request.budget;


import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO Containing Information Needed to Update Budget.")
public class UpdateBudgetRequestDto extends CreateBudgetRequestDTO {
//    @NotBlank(message = "Title should not be blank")
//    @Size(max=100)
//    @Schema(description = "Title", required = true)
//    private String title;

//    @NotBlank
//    @Enum(enumClass = BudgetPeriod.class)
//    @Schema(description = "Period, allowable values (ANNUAL,MONTHLY,WEEKLY,DAILY,CUSTOM)", required = true)
//    private String period;

//    @Schema(description = "Month must be between 1 and 12. Required for MONTHLY period")
//    private short month;
//    @Schema(description = "Year must be 4 xters in len and a number. Required for ANNUAL,MONTHLY period")
//    private short year;
    @Schema(description = "Week Must be between 1 and 4")
    private short week;

//    @Schema(description = "Budget start date in format "+ DateDisplayConstants.DATE_INPUT_FORMAT +". Required for CUSTOM,DAILY,WEEKLY period")
//    private String budgetStartDate;
//
//    @Schema(description = "Budget end date in format "+DateDisplayConstants.DATE_INPUT_FORMAT+".  Required for CUSTOM,DAILY period, Same value for DAILY")
//    private String budgetEndDate;


//    @NotNull(message = "Amount is required")
//    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
//    @Digits(integer = 10, fraction = 2, message = "Amount must be digit with at most two decimal places")
//    @Schema(description = "Amount, must be digit with at most two decimal places", required = true)
//    private BigDecimal amount;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must be digit with at most two decimal places")
    @Schema(description = "Amount, must be digit with at most two decimal places", required = true)
    private BigDecimal totalAmountSpentSoFar;

//    @Schema(description = "budget description")
//    @Size(max = SchemaConstants.BUDGET_DESC_SIZE, message = "Description should be maximum of "+SchemaConstants.BUDGET_DESC_SIZE+" characters")
//    private String description;
}
