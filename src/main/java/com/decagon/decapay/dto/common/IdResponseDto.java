package com.decagon.decapay.DTO.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdResponseDto {
    @Schema(description = "Id for successfully created resource")
    private long id;
}
