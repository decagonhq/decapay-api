package com.decagon.decapay.controller.auth;


import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.auth.AuthResponse;
import com.decagon.decapay.dto.auth.LoginDto;
import com.decagon.decapay.dto.auth.SignOutRequestDto;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.service.auth.LoginServiceImpl;
import com.decagon.decapay.service.auth.TokenBlacklistServiceImpl;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;


@Tag(name ="Auth Controller")
@RequestMapping(value = "${api.basepath-api}")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final LoginServiceImpl loginService;
    private final TokenBlacklistServiceImpl tokenBlacklistService;
    private final UserRepository userRepository;

    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SIGN_IN_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "Validate User credentials to authenticate sign-in and generate token")
    @PostMapping("/signin")
    public ResponseEntity<ApiDataResponse<AuthResponse>> signIn(@Validated @RequestBody LoginDto loginDto) throws Exception {
        String token = loginService.authenticate(loginDto);
        AuthResponse authResponse = new AuthResponse(token);
        return ApiResponseUtil.response(HttpStatus.OK, authResponse, "Sign in successfully");
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SIGN_OUT_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "Sign out user successfully")
    @PostMapping("/signout")
    public ResponseEntity<ApiDataResponse<AuthResponse>> signOut(@RequestBody SignOutRequestDto signOutRequestDto){
        loginService.logOut(signOutRequestDto);
        return ApiResponseUtil.response(HttpStatus.OK,"Sign Out successfully");
    }
}
