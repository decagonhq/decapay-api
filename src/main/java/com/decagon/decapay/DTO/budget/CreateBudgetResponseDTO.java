package com.decagon.decapay.DTO.budget;

import com.decagon.decapay.DTO.common.IdResponseDto;
import lombok.Data;

@Data
public class CreateBudgetResponseDTO extends IdResponseDto {
	public CreateBudgetResponseDTO(long id) {
		super(id);
	}
}
