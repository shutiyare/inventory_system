package com.shutiye.inventory_system.modules.auth.dtos;

import com.shutiye.inventory_system.entity.Permission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Permission entity.
 * Used for transferring permission data in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Permission information")
public class PermissionDTO {
    
    @Schema(description = "Permission ID", example = "1")
    private Long id;
    
    @Schema(description = "Permission name", example = "User View")
    private String name;
    
    @Schema(description = "Permission code", example = "USER_VIEW")
    private String code;
    
    @Schema(description = "Permission description", example = "View users")
    private String description;
    
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
     * Static utility method to convert Permission entity to PermissionDTO.
     */
    public static PermissionDTO fromEntity(Permission permission) {
        if (permission == null) {
            return null;
        }
        
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .code(permission.getCode())
                .description(permission.getDescription())
                .createdDate(permission.getCreatedDate())
                .modifiedDate(permission.getModifiedDate())
                .createdById(permission.getCreatedById())
                .modifiedById(permission.getModifiedById())
                .owner(permission.getOwner())
                .modifier(permission.getModifier())
                .build();
    }
}

