package com.decagon.decapay.service.auth;

import com.decagon.decapay.dto.auth.ChangePasswordRequestDto;
import com.decagon.decapay.dto.auth.CreatePasswordRequestDto;
import com.decagon.decapay.dto.auth.ForgotPasswordRequestDto;
import com.decagon.decapay.dto.auth.VerifyPasswordResetCodeRequest;

public interface PasswordResetService {
    void publishForgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto, String deviceId);

    void verifyPasswordResetCode(VerifyPasswordResetCodeRequest verifyPasswordResetCodeRequest, String deviceId);

    void createPassword(CreatePasswordRequestDto createPasswordRequestDto, String deviceId);

    void changePassword(ChangePasswordRequestDto changePasswordRequestDto, String deviceId);
}
