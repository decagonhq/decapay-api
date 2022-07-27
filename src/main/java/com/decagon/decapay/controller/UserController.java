package com.decagon.decapay.controller;

import static com.decagon.decapay.constants.ResponseMessageConstants.INVALID_REQUEST;
import static com.decagon.decapay.constants.ResponseMessageConstants.USER_EMAIL_ALREADY_EXISTS;
import static com.decagon.decapay.constants.ResponseMessageConstants.USER_SUCCESSFULLY_REGISTERED;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.service.UserService;
import com.decagon.decapay.utils.ApiResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(value = "${api.basepath-api}")
public class UserController {
	private final UserService userService;

	public UserController(final UserService userService) {
		this.userService = userService;
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = USER_SUCCESSFULLY_REGISTERED),
		@ApiResponse(responseCode = "400", description = INVALID_REQUEST),
		@ApiResponse(responseCode = "409", description = USER_EMAIL_ALREADY_EXISTS) })
	@Operation(summary = "Register user", description = "Register new user account with all mandatory fields.")
	@PostMapping("/register")
	public ResponseEntity<ApiDataResponse<User>> registerUser(@Valid @RequestBody UserDTO userRegistrationRequest) {
		return ApiResponseUtil.response(HttpStatus.CREATED, userService.registerUser(userRegistrationRequest),
			USER_SUCCESSFULLY_REGISTERED);
	}
}
