package com.decagon.decapay.service.auth;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.auth.AuthResponse;
import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.dto.auth.SignOutRequestDto;
import com.decagon.decapay.exception.InvalidCredentialException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;


    @Override
    public AuthResponse authenticate(LoginDto loginDto) throws Exception {
        try {
            userAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialException("Invalid Credentials");
        }
        final UserDetails userDetails = signInService.loadUserByUsername(loginDto.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        User user = userRepository.findByEmail(loginDto.getEmail()).get();
        return this.mapToAuthResponse(user, token);
    }

    private AuthResponse mapToAuthResponse(User user, String token) throws JsonProcessingException {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);

        String userSettings = user.getUserSetting();
        UserSettings currentUserSettings = objectMapper.readValue(userSettings, UserSettings.class);
        authResponse.setLanguage(currentUserSettings.getLanguage());
        authResponse.setCountry(currentUserSettings.getCountryCode());
        authResponse.setCurrency(currentUserSettings.getCurrencyCode());

        return authResponse;
    }


    @Override
    public void logOut(SignOutRequestDto signOutRequestDto) {
        this.tokenBlacklistService.blackListToken(signOutRequestDto.getToken());
    }
}
