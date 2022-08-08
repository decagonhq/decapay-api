package com.decagon.decapay.controller.user;

import static com.decagon.decapay.constants.ResponseMessageConstants.INVALID_REQUEST;
import static com.decagon.decapay.constants.ResponseMessageConstants.USER_EMAIL_ALREADY_EXISTS;
import static com.decagon.decapay.constants.ResponseMessageConstants.USER_SUCCESSFULLY_REGISTERED;

import javax.validation.Valid;

import com.decagon.decapay.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.service.user.UserService;
import com.decagon.decapay.utils.ApiResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name ="Register User Controller")
@SecurityRequirements
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class SignUpController {
	private final UserService userService;

	public SignUpController(final UserService userService) {
		this.userService = userService;
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = USER_SUCCESSFULLY_REGISTERED),
		@ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
		@ApiResponse(responseCode = "409", description = USER_EMAIL_ALREADY_EXISTS,content = @Content) })
	@Operation(summary = "Register user", description = "Register new user account with all mandatory fields.")
	@PostMapping("/register")
	public ResponseEntity<ApiDataResponse<User>> registerUser(@Valid @RequestBody UserDTO userRegistrationRequest) {
		return ApiResponseUtil.response(HttpStatus.CREATED, userService.registerUser(userRegistrationRequest),
			USER_SUCCESSFULLY_REGISTERED);
	}
}
