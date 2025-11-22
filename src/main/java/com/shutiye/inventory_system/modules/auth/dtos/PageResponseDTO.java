package com.shutiye.inventory_system.modules.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for paginated responses.
 * Compatible with DataTable format and standard pagination patterns.
 * 
 * @param <T> The type of data items in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Paginated response with data and metadata")
public class PageResponseDTO<T> {
    
    /**
     * List of data items for the current page.
     */
    @Schema(description = "List of data items for the current page")
    private List<T> data;
    
    /**
     * Total number of records (across all pages).
     * Similar to DataTable's recordsTotal
     */
    @Schema(description = "Total number of records (across all pages)", example = "100")
    private Long totalRecords;
    
    /**
     * Total number of records after filtering.
     * Similar to DataTable's recordsFiltered
     */
    @Schema(description = "Total number of records after filtering", example = "50")
    private Long filteredRecords;
    
    /**
     * Current page number (0-indexed).
     */
    @Schema(description = "Current page number (0-indexed)", example = "0")
    private Integer currentPage;
    
    /**
     * Number of records per page.
     */
    @Schema(description = "Number of records per page", example = "10")
    private Integer pageSize;
    
    /**
     * Total number of pages.
     */
    @Schema(description = "Total number of pages", example = "10")
    private Integer totalPages;
    
    /**
     * Whether there is a next page.
     */
    @Schema(description = "Whether there is a next page", example = "true")
    private Boolean hasNext;
    
    /**
     * Whether there is a previous page.
     */
    @Schema(description = "Whether there is a previous page", example = "false")
    private Boolean hasPrevious;
    
    /**
     * Create a PageResponseDTO from Spring Data Page object.
     * 
     * @param data List of data items
     * @param totalRecords Total number of records
     * @param filteredRecords Total number of filtered records
     * @param currentPage Current page number
     * @param pageSize Page size
     * @return PageResponseDTO instance
     */
    public static <T> PageResponseDTO<T> of(List<T> data, 
                                            Long totalRecords, 
                                            Long filteredRecords,
                                            Integer currentPage, 
                                            Integer pageSize) {
        int totalPages = (int) Math.ceil((double) filteredRecords / pageSize);
        
        return PageResponseDTO.<T>builder()
                .data(data)
                .totalRecords(totalRecords)
                .filteredRecords(filteredRecords)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .hasNext(currentPage < totalPages - 1)
                .hasPrevious(currentPage > 0)
                .build();
    }
}


