package com.decagon.decapay.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO containing information needed to create a user. All fields required.")
public class UserDTO {
	@NotBlank
	@Size(min = 1, max = 100, message = "First name field cannot be empty or more than 100 characters")
	@Schema(description = "First name, maximum 100 characters", required = true)
	private String firstName;

	@NotBlank
	@Size(min = 1, max = 100, message = "Last name field cannot be empty or more than 100 characters")
	@Schema(description = "Last name, maximum 100 characters", required = true)
	private String lastName;

	@NotBlank(message = "Email field  cannot be empty")
	@Email
	@Schema(description = "Email", required = true)
	private String email;

	@Size(min = 7, message = "password must be at least seven characters long.")
	@Schema(description = "Password, at least seven characters", required = true)
	private String password;

	@Size(min = 2, max = 2, message = "Country code  must be two characters long.")
	@Schema(description = "Country code  must be two characters long.", required = true)
	private String countryCode;

	@Size(min = 3, max = 3, message = "Currency code  must be three characters long.")
	@Schema(description = "Currency code  must be three characters long.", required = true)
	private String currencyCode;

	@Size(min = 2, max = 2, message = "language must be two characters long.")
	@Schema(description = "language must be two characters long.", required = true)
	private String languageCode;

	@Pattern(regexp = "^\\d{7,15}$", message = "Phone number must be a valid number format")
	@Schema(description = "Phone number, all digits, at least seven and at most 15 characters", required = true)
	private String phoneNumber;


	public UserDTO(String firstName, String lastName, String email, String password, String phoneNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
	}
}

