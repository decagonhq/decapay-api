package com.decagon.decapay.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class CreateBudgetLineItemDto extends BudgetLineItemDto {
    @Schema(description = "The id of the budget category", example = "1", required = true)
    @Positive(message = "Budget category id must be a positive number")
    private Long budgetCategoryId;
}
