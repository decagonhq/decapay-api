package com.decagon.decapay.payloads.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPasswordResetCodeRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 4, max = 4)
    String resetCode;
}
