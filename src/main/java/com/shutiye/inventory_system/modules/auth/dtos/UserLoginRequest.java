package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login request.
 * Contains credentials needed for authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request")
public class UserLoginRequest {
    
    @NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "admin", required = true)
    private String username;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "admin123", required = true)
    private String password;
}

