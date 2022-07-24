package com.decagon.decapay.controller;


import com.decagon.decapay.dto.AuthResponse;
import com.decagon.decapay.dto.LoginDto;
import com.decagon.decapay.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final LoginService loginService;


//    @Operation(summary = "Validate User credentials to authenticate signin and generate token")
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> signIn(@Validated @RequestBody LoginDto loginDto) throws Exception {
        String jwt = loginService.authenticate(loginDto);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}