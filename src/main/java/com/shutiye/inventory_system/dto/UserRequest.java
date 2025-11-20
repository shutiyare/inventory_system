package com.shutiye.inventory_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating and updating User entities
 * Using Java Records for immutability and clean code
 */
@Schema(description = "Request DTO for creating or updating a user")
public record UserRequest(
        @Schema(description = "Unique username for the user", example = "johndoe", required = true)
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
        String username,

        @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Schema(description = "User's first name", example = "John", required = true)
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        String firstName,

        @Schema(description = "User's last name", example = "Doe", required = true)
        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        String lastName,

        @Schema(description = "User's password (minimum 6 characters)", example = "password123", required = true)
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @Schema(description = "User's phone number", example = "+1234567890", required = false)
        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phoneNumber,

        @Schema(description = "Whether the user account is active", example = "true", required = false)
        Boolean active
) {
}

