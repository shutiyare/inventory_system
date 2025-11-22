package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simplified login response DTO.
 * Contains only essential information: access token, refresh token, and minimal user data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Login response containing access token, refresh token, and user information")
public class LoginResponseDTO {
    
    @Schema(description = "JWT access token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String access_token;
    
    @Schema(description = "JWT refresh token for obtaining new access tokens", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refresh_token;
    
    @Schema(description = "Current user information")
    private UserInfo user;
    
    /**
     * Simplified user information for login response.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "User information in login response")
    public static class UserInfo {
        
        @Schema(description = "User ID", example = "1")
        private Long id;
        
        @Schema(description = "Username", example = "admin")
        private String username;
        
        @Schema(description = "Full name", example = "System Administrator")
        private String fullName;
        
        @Schema(description = "Email address", example = "admin@inventory.com")
        private String email;
        
        @Schema(description = "List of role IDs assigned to the user", example = "[1, 2, 3]")
        private List<Long> role_ids;
        
        @Schema(description = "Whether the user account is active", example = "true")
        private Boolean active;
    }
}

