package com.decagon.decapay.service.user;

import com.decagon.decapay.payloads.request.auth.ResetPasswordDto;

public interface UserService {
    void publishForgotPasswordEmail(String email);
}
