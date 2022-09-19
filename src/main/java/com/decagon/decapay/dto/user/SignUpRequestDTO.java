package com.decagon.decapay.dto.user;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO containing information needed to create a user. All fields required.")
public class SignUpRequestDTO extends UserDto {

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


	public SignUpRequestDTO(String firstName, String lastName, String email, String password, String phoneNumber) {
		super(firstName, lastName, email, phoneNumber);
		this.password = password;
	}
}

