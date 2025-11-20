package com.shutiye.inventory_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for User response
 * Using Java Records for immutability and clean code
 */
@Schema(description = "Response DTO containing user information")
public record UserResponse(
        @Schema(description = "Unique identifier for the user", example = "1")
        Long id,
        
        @Schema(description = "Username", example = "johndoe")
        String username,
        
        @Schema(description = "Email address", example = "john.doe@example.com")
        String email,
        
        @Schema(description = "First name", example = "John")
        String firstName,
        
        @Schema(description = "Last name", example = "Doe")
        String lastName,
        
        @Schema(description = "Phone number", example = "+1234567890")
        String phoneNumber,
        
        @Schema(description = "Account active status", example = "true")
        Boolean active,
        
        @Schema(description = "Account creation timestamp", example = "2025-11-20T08:39:16")
        LocalDateTime createdAt,
        
        @Schema(description = "Last update timestamp", example = "2025-11-20T08:39:16")
        LocalDateTime updatedAt
) {
}

