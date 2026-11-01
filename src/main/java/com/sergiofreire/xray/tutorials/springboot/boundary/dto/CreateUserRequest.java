package com.sergiofreire.xray.tutorials.springboot.boundary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload accepted when creating a user. Deliberately has no id field.
 */
public record CreateUserRequest(
        @NotBlank(message = "Name is mandatory") @Size(min = 2) String name,
        @NotBlank(message = "Username is mandatory") @Size(min = 5) String username,
        @NotBlank(message = "Password is mandatory") @Size(min = 5) String password) {
}
