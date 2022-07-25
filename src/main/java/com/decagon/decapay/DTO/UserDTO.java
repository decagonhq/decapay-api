package com.decagon.decapay.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	@NotBlank(message = "first name field  cannot be empty")
	private String firstName;

	@NotBlank(message = "last name field  cannot be empty")
	private String lastName;

	@NotBlank(message = "Email field  cannot be empty")
	@Email
	private String email;

	@Size(min = 7, message = "password must be at least seven characters long.")
	private String password;

	@Pattern(regexp = "^\\d{7,15}$", message = "Phone number must be a valid number format")
	private String phoneNumber;
}

