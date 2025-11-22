package com.shutiye.inventory_system.modules.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login response.
 * Contains JWT token and user information after successful authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponse {
    
    /**
     * JWT token for authentication in subsequent requests.
     * This token should be included in the Authorization header.
     */
    private String token;
    
    /**
     * Type of token (usually "Bearer").
     */
    @Builder.Default
    private String tokenType = "Bearer";
    
    /**
     * User information without sensitive data like password.
     */
    private UserDTO user;
    
    /**
     * Token expiration time in milliseconds.
     * Used by frontend to determine when to refresh the token.
     */
    private Long expiresIn;
}

