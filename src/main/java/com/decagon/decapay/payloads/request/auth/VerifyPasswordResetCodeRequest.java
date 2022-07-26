package com.decagon.decapay.payloads.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPasswordResetCodeRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    String resetCode;
}
