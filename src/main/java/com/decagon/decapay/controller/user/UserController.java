package com.decagon.decapay.controller.user;


import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.EditUserDto;
import com.decagon.decapay.dto.UserResponseDto;
import com.decagon.decapay.dto.budget.ViewBudgetDto;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.ApiResponseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    @GetMapping("/user")
    public ResponseEntity<ApiDataResponse<UserResponseDto>> viewUserProfile(){
       UserResponseDto userResponseDto = userService.viewUserProfile();
        return ApiResponseUtil.response(HttpStatus.OK, userResponseDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = USER_PROFILE_UPDATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "409", description = USER_EMAIL_ALREADY_EXISTS,content = @Content)})
    @Operation(summary = "Edit User Profile", description = "Edit User Profile")
    @PutMapping("/user/edit")
    public ResponseEntity<ApiDataResponse<Object>> editUserProfile(@Valid @RequestBody EditUserDto editUserDto){
        String editUserResponse = userService.editUserProfile(editUserDto);
        return ApiResponseUtil.response(HttpStatus.OK, editUserResponse);
    }
}
