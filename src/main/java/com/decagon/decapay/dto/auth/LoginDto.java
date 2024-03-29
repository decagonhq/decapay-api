package com.decagon.decapay.dto.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginDto {
	@Email
	@NotBlank
	private String email;
	@NotBlank
	private String password;
}
