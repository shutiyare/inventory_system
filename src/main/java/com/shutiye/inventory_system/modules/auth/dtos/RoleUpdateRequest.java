package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data Transfer Object for updating a role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update role information")
public class RoleUpdateRequest {
    
    @Size(max = 100, message = "Role name must not exceed 100 characters")
    @Schema(description = "Role name", example = "MANAGER")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Role description", example = "Manager role with limited access")
    private String description;
    
    @Schema(description = "List of permission IDs to assign to the role", example = "[1, 2, 3]")
    private Set<Long> permissionIds;
    
    @Schema(description = "List of menu IDs to assign to the role", example = "[1, 2, 3]")
    private Set<Long> menuIds;
}

