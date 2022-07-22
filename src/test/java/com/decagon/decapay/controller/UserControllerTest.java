package com.decagon.decapay.controller;

import com.decagon.decapay.dto.LoginDto;
import com.decagon.decapay.service.UserService;
import com.decagon.decapay.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    @Test
    void createAuthenticationToken() throws Exception {


        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("og@gmail.com");
        loginDto.setPassword("1234");


        BDDMockito.when(authenticationManager.authenticate(any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        BDDMockito.when(userService.loadUserByUsername(loginDto.getEmail())).thenReturn(userDetails);
        BDDMockito.when(this.jwtUtil.generateToken(any())).thenReturn("ABC123");
        ResponseEntity<?> authResponse = userController.createAuthenticationToken(loginDto);
        assertTrue(authResponse.hasBody());
        assertTrue(authResponse.getHeaders().isEmpty());
        assertEquals(HttpStatus.OK, authResponse.getStatusCode());
    }
}