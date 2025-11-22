package com.shutiye.inventory_system.modules.auth.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for pagination operations.
 * Provides helper methods for creating Pageable objects and building sort specifications.
 */
public class PaginationHelper {
    
    /**
     * Create a Pageable object from PageRequestDTO.
     * 
     * @param pageRequest Pagination request DTO
     * @return Pageable object for Spring Data JPA
     */
    public static Pageable createPageable(com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO pageRequest) {
        Sort sort = Sort.by(
            pageRequest.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC,
            pageRequest.getSortBy()
        );
        
        return PageRequest.of(
            pageRequest.getPageNumber(),
            pageRequest.getPageSize(),
            sort
        );
    }
    
    /**
     * Create a Pageable object with custom sort.
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sortBy Column name to sort by
     * @param ascending Sort direction
     * @return Pageable object
     */
    public static Pageable createPageable(int page, int size, String sortBy, boolean ascending) {
        Sort sort = Sort.by(
            ascending ? Sort.Direction.ASC : Sort.Direction.DESC,
            sortBy
        );
        
        return PageRequest.of(page, size, sort);
    }
}


