package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating a permission.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update permission information")
public class PermissionUpdateRequest {
    
    @Size(max = 100, message = "Permission name must not exceed 100 characters")
    @Schema(description = "Permission name", example = "User View")
    private String name;
    
    @Size(max = 100, message = "Permission code must not exceed 100 characters")
    @Schema(description = "Permission code", example = "USER_VIEW")
    private String code;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Permission description", example = "View users")
    private String description;
}

