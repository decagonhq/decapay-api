package com.decagon.decapay.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Schema(description = "DTO Containing Information Needed to create New Password. All fields required.")
public class CreatePasswordRequestDto {
    @NotBlank(message = "password should not blank")
    @Size(max=64)
    @Schema(description = "Password", required = true)
    private String password;
    @NotBlank
    @Size(max = 64)
    @Schema(description = "Confirm Password", required = true)
    private String confirmPassword;
    @Schema(description = "Token | For Mobile, Size = 4 | For Web, Size = 36", required = true)
    private String token;
}
