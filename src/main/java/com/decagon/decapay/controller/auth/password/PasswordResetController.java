package com.decagon.decapay.controller.auth.password;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.payloads.request.auth.ForgotPasswordRequestDto;
import com.decagon.decapay.service.auth.PasswordResetService;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.decagon.decapay.constants.AppConstants.*;
import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@RequiredArgsConstructor
@Tag(name ="Password Reset Controller")
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class PasswordResetController {

    private final PasswordResetService service;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = FORGOT_PASSWORD_INITIATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "Publish forgot password email", description = "Publish forgot password email with RequestHeader key = DVC_KY_HDR and value = MOBILE_DEVICE_ID = 1 | WEB_DEVICE_ID = 2")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiDataResponse<Object>> initiateForgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto,
                                                                          @RequestHeader(DEVICE_KEY_HEADER) String deviceId) {
        this.service.publishForgotPassword(forgotPasswordRequestDto, deviceId);
        return ApiResponseUtil.response(HttpStatus.OK, FORGOT_PASSWORD_INITIATED_SUCCESSFULLY);
    }

}
