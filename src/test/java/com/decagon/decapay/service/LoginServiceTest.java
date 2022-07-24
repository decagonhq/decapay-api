package com.decagon.decapay.service;

import com.decagon.decapay.dto.LoginDto;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

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
    private UserService signInService;

    @InjectMocks
    private LoginService loginService;

    @Test
    void authenticate() throws Exception {

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("og@gmail.com");
        loginDto.setPassword("1234");

        User user = new User();
        user.setEmail("og@gmail.com");
        user.setPassword("1234");

        BDDMockito.when(authenticationManager.authenticate(any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        BDDMockito.when(signInService.loadUserByUsername(loginDto.getEmail())).thenReturn(userDetails);
        BDDMockito.when(this.jwtUtil.generateToken(userDetails)).thenReturn("ABC123");
        String jwt = loginService.authenticate(loginDto);
        assertThat(jwt).isNotNull();
        assertEquals("ABC123", jwt);
    }
}