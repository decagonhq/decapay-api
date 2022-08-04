package com.decagon.decapay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateBudgetResponseDTO {
	@Schema(description = "Budget Id for successfully created budget")
	private long budgetID;
}
