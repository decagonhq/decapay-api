package com.decagon.decapay.dto.user;


import com.decagon.decapay.constants.SchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@Schema(description = "DTO containing information needed to create and edit a user")
public class UserDto {

    @NotBlank
    @Size(max = SchemaConstants.FIRST_NAME_MAX_SIZE, message = "First name field cannot be empty or more than "+SchemaConstants.FIRST_NAME_MAX_SIZE+" characters")
    @Schema(description = "First name, maximum "+ SchemaConstants.FIRST_NAME_MAX_SIZE +" characters", required = true)
    private String firstName;

    @NotBlank
    @Size(max = SchemaConstants.LAST_NAME_MAX_SIZE, message = "Last name field cannot be empty or more than "+SchemaConstants.LAST_NAME_MAX_SIZE+" characters")
    @Schema(description = "Last name, maximum "+SchemaConstants.LAST_NAME_MAX_SIZE+" characters", required = true)
    private String lastName;

    @NotBlank(message = "Email field  cannot be empty")
    @Size(max =SchemaConstants.EMAIL_MAX_SIZE)
    @Email
    @Schema(description = "Email", required = true)
    private String email;

    @NotEmpty
    //@Pattern(regexp = "^\\d{7,15}$", message = "Phone number must be a valid number format")
    @Size(max =SchemaConstants.PHONE_NUMBER_MAX_SIZE)
    @Schema(description = "Phone number, all digits, at least seven and at most 15 characters", required = true)
    private String phoneNumber;

    public UserDto(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}