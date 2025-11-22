package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating a menu.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update menu information")
public class MenuUpdateRequest {
    
    @Size(max = 100, message = "Menu title must not exceed 100 characters")
    @Schema(description = "Menu title", example = "Dashboard")
    private String title;
    
    @Size(max = 255, message = "Path must not exceed 255 characters")
    @Schema(description = "Menu path", example = "/dashboard")
    private String path;
    
    @Size(max = 100, message = "Icon must not exceed 100 characters")
    @Schema(description = "Menu icon", example = "dashboard")
    private String icon;
    
    @Schema(description = "Display order index", example = "1")
    private Integer orderIndex;
    
    @Schema(description = "Parent menu ID (null for root menus)", example = "1")
    private Long parentId;
}

