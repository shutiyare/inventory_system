package com.shutiye.inventory_system.modules.auth.dtos;

import com.shutiye.inventory_system.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for User entity.
 * Never exposes password for security reasons.
 * Used for transferring user data in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User information")
public class UserDTO {
    
    @Schema(description = "User ID", example = "1")
    private Long id;
    
    @Schema(description = "Username", example = "admin")
    private String username;
    
    @Schema(description = "Email address", example = "admin@inventory.com")
    private String email;
    
    @Schema(description = "Full name", example = "System Administrator")
    private String fullName;
    
    @Schema(description = "Whether the user account is active", example = "true")
    private Boolean active;
    
    @Schema(description = "Roles assigned to the user")
    private Set<RoleDTO> roles;
    
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
     * Static utility method to convert User entity to UserDTO.
     * This method ensures password is never included in the DTO.
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .active(user.getActive())
                .roles(user.getRoles() != null ? 
                    user.getRoles().stream()
                        .map(RoleDTO::fromEntity)
                        .collect(Collectors.toSet()) : null)
                .createdDate(user.getCreatedDate())
                .modifiedDate(user.getModifiedDate())
                .createdById(user.getCreatedById())
                .modifiedById(user.getModifiedById())
                .owner(user.getOwner())
                .modifier(user.getModifier())
                .build();
    }
}

