package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.RoleCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.RoleDTO;
import com.shutiye.inventory_system.modules.auth.dtos.RoleUpdateRequest;

import java.util.List;
import java.util.Set;

/**
 * Service interface for Role operations.
 */
public interface RoleService {
    
    /**
     * Create a new role.
     * 
     * @param request Role creation request
     * @return Created role DTO
     */
    RoleDTO createRole(RoleCreateRequest request);
    
    /**
     * Update an existing role.
     * 
     * @param id Role ID
     * @param request Role update request
     * @return Updated role DTO
     */
    RoleDTO updateRole(Long id, RoleUpdateRequest request);
    
    /**
     * Delete a role by ID.
     * 
     * @param id Role ID
     */
    void deleteRole(Long id);
    
    /**
     * Assign permissions to a role.
     * 
     * @param roleId ID of the role
     * @param permissionIds Set of permission IDs to assign
     * @return Updated role DTO with assigned permissions
     */
    RoleDTO assignPermissions(Long roleId, Set<Long> permissionIds);
    
    /**
     * Assign menus to a role.
     * 
     * @param roleId ID of the role
     * @param menuIds Set of menu IDs to assign
     * @return Updated role DTO with assigned menus
     */
    RoleDTO assignMenus(Long roleId, Set<Long> menuIds);
    
    /**
     * Get all roles in the system (without pagination).
     * 
     * @return List of all role DTOs
     */
    List<RoleDTO> getAllRoles();
    
    /**
     * Get roles with pagination, search, and filtering.
     * 
     * @param pageRequest Pagination, search, and filter request
     * @return Paginated response with role DTOs
     */
    PageResponseDTO<RoleDTO> getAllRoles(PageRequestDTO pageRequest);
    
    /**
     * Get a role by ID.
     * 
     * @param id Role ID
     * @return Role DTO if found
     */
    RoleDTO getRoleById(Long id);
}

