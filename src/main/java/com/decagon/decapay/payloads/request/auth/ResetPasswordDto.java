package com.decagon.decapay.payloads.request.auth;

import lombok.Data;

@Data
public class ResetPasswordDto {

    private String newPassword;
    private String confirmPassword;
    private String token;
}
