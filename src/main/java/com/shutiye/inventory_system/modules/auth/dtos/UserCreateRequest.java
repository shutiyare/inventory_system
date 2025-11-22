package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data Transfer Object for creating a new user.
 * Contains validation annotations to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new user")
public class UserCreateRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Schema(description = "Username", example = "john_doe", required = true)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "Email address", example = "john.doe@example.com", required = true)
    private String email;
    
    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    @Schema(description = "Full name", example = "John Doe", required = true)
    private String fullName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Schema(description = "Password", example = "SecurePass123!", required = true)
    private String password;
    
    /**
     * Optional set of role IDs to assign to the user during creation.
     * If not provided, user will be created without roles.
     */
    @Schema(description = "List of role IDs to assign to the user", example = "[1, 2]")
    private Set<Long> roleIds;
    
    /**
     * Whether the user should be active upon creation.
     * Defaults to true if not specified.
     */
    @Schema(description = "Whether the user account is active", example = "true")
    private Boolean active;
}

