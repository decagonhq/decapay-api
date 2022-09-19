package com.decagon.decapay.controller.auth;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.auth.ChangePasswordRequestDto;
import com.decagon.decapay.service.auth.PasswordResetService;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.decagon.decapay.constants.AppConstants.DEVICE_KEY_HEADER;
import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@SecurityRequirements
@RequiredArgsConstructor
@Tag(name ="Change Password Controller")
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class ChangePasswordController {

    private final PasswordResetService service;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = PASSWORD_CHANGED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "404", description = NOT_FOUND,content = @Content)})
    @Operation(summary = "Change Password", description = "Change User Password with RequestHeader key = DVC_KY_HDR and value = MOBILE_DEVICE_ID = 1 | WEB_DEVICE_ID = 2")
    @PostMapping("/change-password")
    public ResponseEntity<ApiDataResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto,
                                                                  @RequestHeader(DEVICE_KEY_HEADER) String deviceId) {
        this.service.changePassword(changePasswordRequestDto, deviceId);
        return ApiResponseUtil.response(HttpStatus.OK, PASSWORD_CHANGED_SUCCESSFULLY);
    }
}
