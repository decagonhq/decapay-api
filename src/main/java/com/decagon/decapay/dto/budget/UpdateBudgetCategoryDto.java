package com.decagon.decapay.dto.budget;

import com.decagon.decapay.constants.SchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateBudgetCategoryDto {
    @NotNull(message = "Title is required")
    @Size(min = 1, max = SchemaConstants.BUDGET_CATEGORY_TITLE_SIZE, message = "Title field cannot be empty or more than "+SchemaConstants.BUDGET_CATEGORY_TITLE_SIZE+" characters")
    @Schema(description = "Title, maximum "+SchemaConstants.BUDGET_CATEGORY_TITLE_SIZE+" characters", required = true)
    private String title;
}
