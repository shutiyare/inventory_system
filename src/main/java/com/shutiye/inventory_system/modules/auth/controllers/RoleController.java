package com.shutiye.inventory_system.modules.auth.controllers;

import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.RoleCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.RoleDTO;
import com.shutiye.inventory_system.modules.auth.dtos.RoleUpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.RoleService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * REST controller for role management operations.
 * Requires authentication for all endpoints.
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Role management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    
    private final RoleService roleService;
    
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    /**
     * Create a new role.
     * 
     * @param request Role creation request
     * @return Created role DTO
     */
    @PostMapping
    @Operation(summary = "Create role", description = "Create a new role")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleCreateRequest request) {
        logger.info("Creating role: {}", request.getName());
        RoleDTO role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }
    
    /**
     * Update a role.
     * 
     * @param id Role ID
     * @param request Role update request
     * @return Updated role DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update role", description = "Update role information")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id,
                                             @Valid @RequestBody RoleUpdateRequest request) {
        logger.info("Updating role with ID: {}", id);
        RoleDTO role = roleService.updateRole(id, request);
        return ResponseEntity.ok(role);
    }
    
    /**
     * Delete a role.
     * 
     * @param id Role ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Delete a role by ID")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        logger.info("Deleting role with ID: {}", id);
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all roles (without pagination - for backward compatibility).
     * 
     * @return List of all role DTOs
     */
    @GetMapping("/all")
    @Operation(summary = "Get all roles (no pagination)", description = "Retrieve all roles in the system without pagination")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        logger.debug("Fetching all roles");
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
    
    /**
     * Get roles with pagination, search, and filtering.
     * 
     * @param pageRequest Pagination, search, and filter parameters
     * @return Paginated response with role DTOs
     */
    @GetMapping
    @Operation(summary = "Get roles (paginated)", 
               description = "Retrieve roles with pagination, search, and filtering. " +
                           "Supports: page, size, search (global), sortBy, sortDir, filters (field-specific)")
    public ResponseEntity<PageResponseDTO<RoleDTO>> getAllRolesPaginated(
            @ModelAttribute PageRequestDTO pageRequest) {
        logger.debug("Fetching roles with pagination: page={}, size={}, search={}", 
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
        PageResponseDTO<RoleDTO> response = roleService.getAllRoles(pageRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get role by ID.
     * 
     * @param id Role ID
     * @return Role DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieve a specific role by ID")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        logger.debug("Fetching role with ID: {}", id);
        RoleDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }
    
    /**
     * Assign permissions to a role.
     * 
     * @param id Role ID
     * @param permissionIds Set of permission IDs to assign
     * @return Updated role DTO with assigned permissions
     */
    @PutMapping("/{id}/assign-permissions")
    @Operation(summary = "Assign permissions to role", description = "Assign one or more permissions to a role")
    public ResponseEntity<RoleDTO> assignPermissions(@PathVariable Long id,
                                                     @RequestBody Set<Long> permissionIds) {
        logger.info("Assigning permissions to role with ID: {}", id);
        RoleDTO role = roleService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok(role);
    }
    
    /**
     * Assign menus to a role.
     * 
     * @param id Role ID
     * @param menuIds Set of menu IDs to assign
     * @return Updated role DTO with assigned menus
     */
    @PutMapping("/{id}/assign-menus")
    @Operation(summary = "Assign menus to role", description = "Assign one or more menus to a role")
    public ResponseEntity<RoleDTO> assignMenus(@PathVariable Long id,
                                              @RequestBody Set<Long> menuIds) {
        logger.info("Assigning menus to role with ID: {}", id);
        RoleDTO role = roleService.assignMenus(id, menuIds);
        return ResponseEntity.ok(role);
    }
}

