package com.shutiye.inventory_system.modules.auth.dtos;

import com.shutiye.inventory_system.entity.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Menu entity.
 * Used for transferring menu data in API responses.
 * Supports nested menu structure for hierarchical menus.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Menu information")
public class MenuDTO {
    
    @Schema(description = "Menu ID", example = "1")
    private Long id;
    
    @Schema(description = "Menu title", example = "Dashboard")
    private String title;
    
    @Schema(description = "Menu path", example = "/dashboard")
    private String path;
    
    @Schema(description = "Menu icon", example = "dashboard")
    private String icon;
    
    @Schema(description = "Display order index", example = "1")
    private Integer orderIndex;
    
    @Schema(description = "Parent menu ID", example = "1")
    private Long parentId;
    
    @Schema(description = "Parent menu title", example = "Settings")
    private String parentTitle;
    
    @Schema(description = "Child menus")
    private Set<MenuDTO> children;
    
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
     * Static utility method to convert Menu entity to MenuDTO.
     * Recursively converts children menus to create a tree structure.
     */
    public static MenuDTO fromEntity(Menu menu) {
        if (menu == null) {
            return null;
        }
        
        return MenuDTO.builder()
                .id(menu.getId())
                .title(menu.getTitle())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .orderIndex(menu.getOrderIndex())
                .parentId(menu.getParent() != null ? menu.getParent().getId() : null)
                .parentTitle(menu.getParent() != null ? menu.getParent().getTitle() : null)
                .children(menu.getChildren() != null && !menu.getChildren().isEmpty() ? 
                    menu.getChildren().stream()
                        .map(MenuDTO::fromEntity)
                        .collect(Collectors.toSet()) : null)
                .createdDate(menu.getCreatedDate())
                .modifiedDate(menu.getModifiedDate())
                .createdById(menu.getCreatedById())
                .modifiedById(menu.getModifiedById())
                .owner(menu.getOwner())
                .modifier(menu.getModifier())
                .build();
    }
}

