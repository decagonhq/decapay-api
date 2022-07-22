package com.decagon.decapay.service;

import com.decagon.decapay.dto.LoginDto;
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
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService signInService;


    public String createAuthenticationToken(LoginDto loginDto) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("incorrect username or password!");
        }
        final UserDetails userDetails = signInService.loadUserByUsername(loginDto.getEmail());
        return jwtUtil.generateToken(userDetails);
    }
}
