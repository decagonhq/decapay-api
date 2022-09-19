package com.decagon.decapay.dto.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO Containing Information Needed to Change Password. All fields required.")
public class ChangePasswordRequestDto {
    @NotBlank(message = "password should not blank")
    @Size(max=64)
    @Schema(description = "Password", required = true)
    private String password;

    @NotBlank(message = "new password should not be blank")
    @Size(max=64)
    @Schema(description = "New Password", required = true)
    private String newPassword;

    @NotBlank(message = "Confirm New password should not be blank")
    @Size(max = 64)
    @Schema(description = "Confirm New Password", required = true)
    private String confirmNewPassword;
}
