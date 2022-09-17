package com.decagon.decapay.service.auth;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.constants.AppConstants;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Slf4j
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
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(()->new InvalidCredentialException("Invalid Credentials"));
        return this.mapToAuthResponse(user, token);
    }

    private AuthResponse mapToAuthResponse(User user, String token) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        String userSettingsJsonStr = user.getUserSetting();
        String languageCode= AppConstants.DEFAULT_LANGUAGE;
        String currencyCode=AppConstants.DEFAULT_CURRENCY_CODE;
        String countryCode=AppConstants.DEFAULT_COUNTRY;
        if (userSettingsJsonStr != null) {
            try {
                UserSettings currentUserSettings = objectMapper.readValue(userSettingsJsonStr, UserSettings.class);
                languageCode=currentUserSettings.getLanguage();
                currencyCode=currentUserSettings.getCurrencyCode();
                countryCode=currentUserSettings.getCountryCode();
            } catch (Exception e) {
             log.error("Error converting user settings json string",e);
            }
        }
        authResponse.setLanguage(languageCode);
        authResponse.setCountry(countryCode);
        authResponse.setCurrency(currencyCode);
        return authResponse;
    }


    @Override
    public void logOut(SignOutRequestDto signOutRequestDto) {
        this.tokenBlacklistService.blackListToken(signOutRequestDto.getToken());
    }
}
