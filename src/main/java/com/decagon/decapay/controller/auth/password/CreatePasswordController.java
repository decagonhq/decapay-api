package com.decagon.decapay.controller.auth.password;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.payloads.request.auth.CreatePasswordRequestDto;
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

import static com.decagon.decapay.constants.AppConstants.DEVICE_KEY_HEADER;
import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
@Tag(name ="Create Password Controller")
public class CreatePasswordController {
    private final PasswordResetService service;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = PASSWORD_CREATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "Create Password", description = "Create new password | RequestHeader key = 'DVC_KY_HDR' and value = 'MOBILE_DEVICE_ID = 1 | WEB_DEVICE_ID = 2'")
    @PostMapping("/create-password")
    public ResponseEntity<ApiDataResponse<Object>> createPassword(@Valid @RequestBody CreatePasswordRequestDto createPasswordRequestDto,
                                                                          @RequestHeader(DEVICE_KEY_HEADER) String deviceId) {
        this.service.createPassword(createPasswordRequestDto, deviceId);
        return ApiResponseUtil.response(HttpStatus.OK, PASSWORD_CREATED_SUCCESSFULLY);
    }
}
