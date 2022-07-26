package com.decagon.decapay.service;

import com.decagon.decapay.dto.LoginDto;
import com.decagon.decapay.exception.InvalidCredentialException;
import com.decagon.decapay.model.user.Auth;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repository.AuthRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
public class LoginService {
    private final AuthenticationManager userAuthenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService signInService;



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
}
