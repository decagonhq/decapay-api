package com.decagon.decapay.dto.budget;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.constants.SchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO containing information needed to Log Expense.")
public class ExpenseDto extends BudgetLineItemDto {
    @Schema(description = "Expense description", required = true)
    @Size(max = SchemaConstants.EXPENSE_DESC_SIZE, message = "Description should be maximum of " + SchemaConstants.EXPENSE_DESC_SIZE + " characters")
    @NotBlank
    private String description;
    @Schema(description = "Expense Log date in format " + DateDisplayConstants.DATE_INPUT_FORMAT, required = true)
    @NotBlank
    private String transactionDate;
}
