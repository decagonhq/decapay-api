package com.decagon.decapay.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Schema(description = "DTO containing information needed to edit a user profile")
public class EditUserDto {

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

    @Pattern(regexp = "^\\d{7,15}$", message = "Phone number must be a valid number format")
    @Schema(description = "Phone number, all digits, at least seven and at most 15 characters", required = true)
    private String phoneNumber;
}
