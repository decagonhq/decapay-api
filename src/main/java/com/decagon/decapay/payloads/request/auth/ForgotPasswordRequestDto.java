package com.decagon.decapay.payloads.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequestDto {
    @NotBlank
    @Email
    private String email;
}
