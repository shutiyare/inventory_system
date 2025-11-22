package com.shutiye.inventory_system.modules.auth.dtos;

import com.shutiye.inventory_system.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Role entity.
 * Used for transferring role data in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Role information")
public class RoleDTO {
    
    @Schema(description = "Role ID", example = "1")
    private Long id;
    
    @Schema(description = "Role name", example = "SUPER_ADMIN")
    private String name;
    
    @Schema(description = "Role description", example = "Super Administrator with full system access")
    private String description;
    
    @Schema(description = "Permissions assigned to this role")
    private Set<PermissionDTO> permissions;
    
    @Schema(description = "Menus accessible by this role")
    private Set<MenuDTO> menus;
    
    @Schema(description = "Creation date", example = "2025-11-22T10:00:00")
    private LocalDateTime createdDate;
    
    @Schema(description = "Last modification date", example = "2025-11-22T10:00:00")
    private LocalDateTime modifiedDate;
    
    @Schema(description = "ID of user who created this record", example = "1")
    private Long createdById;
    
    @Schema(description = "ID of user who last modified this record", example = "1")
    private Long modifiedById;
    
    @Schema(description = "Username of creator", example = "admin")
    private String owner;
    
    @Schema(description = "Username of last modifier", example = "admin")
    private String modifier;
    
    /**
     * Static utility method to convert Role entity to RoleDTO.
     * Includes permissions and menus if they are loaded.
     */
    public static RoleDTO fromEntity(Role role) {
        if (role == null) {
            return null;
        }
        
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions() != null ? 
                    role.getPermissions().stream()
                        .map(PermissionDTO::fromEntity)
                        .collect(Collectors.toSet()) : null)
                .menus(role.getMenus() != null ? 
                    role.getMenus().stream()
                        .map(MenuDTO::fromEntity)
                        .collect(Collectors.toSet()) : null)
                .createdDate(role.getCreatedDate())
                .modifiedDate(role.getModifiedDate())
                .createdById(role.getCreatedById())
                .modifiedById(role.getModifiedById())
                .owner(role.getOwner())
                .modifier(role.getModifier())
                .build();
    }
}

