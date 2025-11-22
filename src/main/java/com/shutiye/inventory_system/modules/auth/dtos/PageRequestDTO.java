package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object for pagination, search, and filtering requests.
 * Compatible with DataTable format and standard pagination patterns.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Pagination, search, and filter request parameters")
public class PageRequestDTO {
    
    /**
     * Page number (0-indexed). Default: 0
     */
    @Schema(description = "Page number (0-indexed)", example = "0", defaultValue = "0")
    @Builder.Default
    private Integer page = 0;
    
    /**
     * Number of records per page. Default: 10
     */
    @Schema(description = "Number of records per page", example = "10", defaultValue = "10")
    @Builder.Default
    private Integer size = 10;
    
    /**
     * Global search term to search across all searchable fields.
     * Similar to DataTable's search[value]
     */
    @Schema(description = "Global search term to search across all searchable fields", example = "john")
    private String search;
    
    /**
     * Sort column name. Default: "id"
     */
    @Schema(description = "Column name to sort by", example = "username", defaultValue = "id")
    @Builder.Default
    private String sortBy = "id";
    
    /**
     * Sort direction: "asc" or "desc". Default: "asc"
     */
    @Schema(description = "Sort direction", example = "asc", allowableValues = {"asc", "desc"}, defaultValue = "asc")
    @Builder.Default
    private String sortDir = "asc";
    
    /**
     * Field-specific filters.
     * Key: field name, Value: filter value
     * Example: {"active": "true", "roleId": "1"}
     */
    @Schema(description = "Field-specific filters as key-value pairs", example = "{\"active\": \"true\", \"roleId\": \"1\"}")
    @Builder.Default
    private Map<String, String> filters = new HashMap<>();
    
    /**
     * Get sort direction as boolean for Spring Data JPA.
     * @return true for ascending, false for descending
     */
    public boolean isAscending() {
        return !"desc".equalsIgnoreCase(sortDir);
    }
    
    /**
     * Get page number (ensuring non-negative).
     * @return page number
     */
    public int getPageNumber() {
        return Math.max(0, page);
    }
    
    /**
     * Get page size (ensuring positive and within limits).
     * @return page size
     */
    public int getPageSize() {
        int sizeValue = size != null && size > 0 ? size : 10;
        return Math.min(sizeValue, 100); // Max 100 records per page
    }
}


