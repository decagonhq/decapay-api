package com.decagon.decapay.controller.user;


import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.auth.ChangePasswordRequestDto;
import com.decagon.decapay.dto.user.UserResponseDto;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.decagon.decapay.constants.AppConstants.AUTHORIZATION;
import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@Tag(name = "User Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class UserController {

    private final UserService userService;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESOURCE_RETRIEVED_SUCCESSFULLY),
            @ApiResponse(responseCode = "404", description = NOT_FOUND,content = @Content)})
    @Operation(summary = "View User Profile", description = "View User Profile")
    @GetMapping("/profile")
    public ResponseEntity<ApiDataResponse<UserResponseDto>> viewUserProfile(){
       UserResponseDto userResponseDto = userService.viewUserProfile();
        return ApiResponseUtil.response(HttpStatus.OK, userResponseDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = PASSWORD_CHANGED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "404", description = NOT_FOUND,content = @Content)})
    @Operation(summary = "Change Password", description = "Change User Password With Token Passed In Header")
    @PostMapping("/profile/changePassword")
    public ResponseEntity<ApiDataResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto,
                                                                  @RequestHeader(AUTHORIZATION) String token) {
        this.userService.changePassword(changePasswordRequestDto, token);
        return ApiResponseUtil.response(HttpStatus.OK, PASSWORD_CHANGED_SUCCESSFULLY);
    }

}
