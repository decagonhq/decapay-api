package com.decagon.decapay.controller.auth.password;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.payloads.request.auth.ForgotPasswordRequestDto;
import com.decagon.decapay.service.auth.PasswordResetService;
import com.decagon.decapay.utils.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.decagon.decapay.constants.ResponseMessageConstants.FORGOT_PASSWORD_INITIATED_SUCCESSFULLY;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class PasswordResetController implements IPasswordResetController {

    private final PasswordResetService service;

    @Override
    @PostMapping("/forgot-password/{deviceId}")
    public ResponseEntity<ApiDataResponse<Object>> initiateForgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto, @PathVariable String deviceId) {
        switch (deviceId) {
            case "1" -> this.service.publishForgotPasswordResetEmail(forgotPasswordRequestDto.getEmail());
            case "2" -> this.service.publishForgotPasswordResetCodeEmail(forgotPasswordRequestDto.getEmail());
        }
        return ApiResponseUtil.response(HttpStatus.OK, FORGOT_PASSWORD_INITIATED_SUCCESSFULLY);
    }

}
