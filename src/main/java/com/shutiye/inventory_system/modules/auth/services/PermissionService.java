package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionUpdateRequest;

import java.util.List;

/**
 * Service interface for Permission operations.
 */
public interface PermissionService {
    
    /**
     * Create a new permission.
     * 
     * @param request Permission creation request
     * @return Created permission DTO
     */
    PermissionDTO createPermission(PermissionCreateRequest request);
    
    /**
     * Update an existing permission.
     * 
     * @param id Permission ID
     * @param request Permission update request
     * @return Updated permission DTO
     */
    PermissionDTO updatePermission(Long id, PermissionUpdateRequest request);
    
    /**
     * Delete a permission by ID.
     * 
     * @param id Permission ID
     */
    void deletePermission(Long id);
    
    /**
     * Get all permissions in the system (without pagination).
     * 
     * @return List of all permission DTOs
     */
    List<PermissionDTO> getAllPermissions();
    
    /**
     * Get permissions with pagination, search, and filtering.
     * 
     * @param pageRequest Pagination, search, and filter request
     * @return Paginated response with permission DTOs
     */
    PageResponseDTO<PermissionDTO> getAllPermissions(PageRequestDTO pageRequest);
    
    /**
     * Get a permission by ID.
     * 
     * @param id Permission ID
     * @return Permission DTO if found
     */
    PermissionDTO getPermissionById(Long id);
    
    /**
     * Get a permission by code.
     * 
     * @param code Permission code
     * @return Permission DTO if found
     */
    PermissionDTO getPermissionByCode(String code);
}

