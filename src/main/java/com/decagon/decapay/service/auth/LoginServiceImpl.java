package com.decagon.decapay.service.auth;

import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.dto.auth.SignOutRequestDto;
import com.decagon.decapay.exception.InvalidCredentialException;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager userAuthenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService signInService;

    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public String authenticate(LoginDto loginDto) throws Exception {
        try {
            userAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialException("Invalid Credentials");
        }
        final UserDetails userDetails = signInService.loadUserByUsername(loginDto.getEmail());
        return jwtUtil.generateToken(userDetails);
    }

    @Override
    public void logOut(SignOutRequestDto signOutRequestDto) {
        this.tokenBlacklistService.blackListToken(signOutRequestDto.getToken());
    }
}
