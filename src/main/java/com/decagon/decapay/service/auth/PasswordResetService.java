package com.decagon.decapay.service.auth;

import com.decagon.decapay.payloads.request.auth.ForgotPasswordRequestDto;
import com.decagon.decapay.payloads.request.auth.VerifyPasswordResetCodeRequest;

public interface PasswordResetService {
    void publishForgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto, String deviceId);

    void verifyPasswordResetCode(VerifyPasswordResetCodeRequest verifyPasswordResetCodeRequest, String deviceId);
}
