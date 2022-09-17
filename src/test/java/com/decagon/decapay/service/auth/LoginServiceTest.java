package com.decagon.decapay.service.auth;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.auth.AuthResponse;
import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import com.decagon.decapay.utils.TestModels;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;

    @Mock
    private CustomUserDetailsService signInService;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ObjectMapper objectMapper;
    private final UserSettings userSettings = TestModels.userSettings("en", "NG", "NGN");


    @Test
    void authenticate() throws Exception {

       /* LoginDto loginDto = new LoginDto();
        loginDto.setEmail("og@gmail.com");
        loginDto.setPassword("1234");

        User user = new User();
        user.setEmail("og@gmail.com");
        user.setPassword("1234");
        //user.setUserSetting(userSettings.toJSONString());


        BDDMockito.when(authenticationManager.authenticate(any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        BDDMockito.when(signInService.loadUserByUsername(loginDto.getEmail())).thenReturn(userDetails);
        BDDMockito.when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        BDDMockito.when(this.objectMapper.readValue(user.getUserSetting(), UserSettings.class)).thenReturn(userSettings);
        BDDMockito.when(this.jwtUtil.generateToken(userDetails)).thenReturn("ABC123");
        AuthResponse authResponse = loginService.authenticate(loginDto);
        assertThat(authResponse).isNotNull();

        UserSettings settings = new UserSettings();
        settings.setLanguage("en");
        settings.setCountryCode("NG");
        settings.setCurrencyCode("NGN");
        //assertEquals(settings.toJSONString(), user.getUserSetting());
        */

    }
}