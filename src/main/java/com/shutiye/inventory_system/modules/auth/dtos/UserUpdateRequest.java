package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data Transfer Object for updating a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update user information")
public class UserUpdateRequest {
    
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Email(message = "Email must be valid")
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;
    
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    @Schema(description = "Full name", example = "John Doe")
    private String fullName;
    
    @Schema(description = "Whether the user account is active", example = "true")
    private Boolean active;
    
    @Schema(description = "List of role IDs to assign to the user", example = "[1, 2]")
    private Set<Long> roleIds;
}

