package com.decagon.decapay.controller.auth.password;


import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.payloads.request.auth.ForgotPasswordRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@Tag(name ="Password Reset Controller")
public interface IPasswordResetController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = FORGOT_PASSWORD_INITIATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "Publish forgot password email", description = "Publish forgot password email")
    ResponseEntity<ApiDataResponse<Object>> initiateForgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto);

}
