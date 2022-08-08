package com.decagon.decapay.dto.budget;

import com.decagon.decapay.dto.common.IdResponseDto;
import lombok.Data;

@Data
public class CreateBudgetResponseDTO extends IdResponseDto {
	public CreateBudgetResponseDTO(long id) {
		super(id);
	}
}
