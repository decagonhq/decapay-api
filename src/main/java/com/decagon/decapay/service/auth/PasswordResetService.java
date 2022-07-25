package com.decagon.decapay.service.auth;

import com.decagon.decapay.payloads.request.auth.ForgotPasswordRequestDto;

public interface PasswordResetService {
    void publishForgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto, String deviceId);
}
