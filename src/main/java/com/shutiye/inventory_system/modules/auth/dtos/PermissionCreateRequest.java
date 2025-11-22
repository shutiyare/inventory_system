package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new permission.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new permission")
public class PermissionCreateRequest {
    
    @NotBlank(message = "Permission name is required")
    @Size(max = 100, message = "Permission name must not exceed 100 characters")
    @Schema(description = "Permission name", example = "User View", required = true)
    private String name;
    
    @NotBlank(message = "Permission code is required")
    @Size(max = 100, message = "Permission code must not exceed 100 characters")
    @Schema(description = "Permission code", example = "USER_VIEW", required = true)
    private String code;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Permission description", example = "View users")
    private String description;
}

