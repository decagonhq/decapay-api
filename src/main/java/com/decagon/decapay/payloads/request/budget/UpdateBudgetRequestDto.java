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

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must be digit with at most two decimal places")
    @Schema(description = "Amount, must be digit with at most two decimal places", required = true)
    private BigDecimal totalAmountSpentSoFar;
}
