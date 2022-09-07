package com.decagon.decapay.service.auth;

import com.decagon.decapay.dto.auth.AuthResponse;
import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.dto.auth.SignOutRequestDto;

public interface LoginService {
    AuthResponse authenticate(LoginDto loginDto) throws Exception;

    void logOut(SignOutRequestDto signOutRequestDto);
}