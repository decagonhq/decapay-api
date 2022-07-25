package com.decagon.decapay.service.auth;

public interface PasswordResetService {
    void publishForgotPasswordResetEmail(String email);

    void publishForgotPasswordResetCodeEmail(String email);
}
