package com.decagon.decapay.service.auth;

import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.dto.auth.SignOutRequestDto;

public interface LoginService {
    String authenticate(LoginDto loginDto) throws Exception;

    void logOut(SignOutRequestDto signOutRequestDto);
}