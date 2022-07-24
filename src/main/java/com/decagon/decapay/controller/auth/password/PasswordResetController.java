package com.decagon.decapay.controller.auth.password;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.payloads.request.auth.ForgotPasswordRequestDto;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.decagon.decapay.constants.ResponseMessageConstants.FORGOT_PASSWORD_INITIATED_SUCCESSFULLY;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class PasswordResetController implements IPasswordResetController {

    private final UserService userService;

    @Override
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiDataResponse<Object>> initiateForgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        this.userService.publishForgotPasswordEmail(forgotPasswordRequestDto.getEmail());
        return ApiResponseUtil.response(HttpStatus.OK, FORGOT_PASSWORD_INITIATED_SUCCESSFULLY);
    }

}
