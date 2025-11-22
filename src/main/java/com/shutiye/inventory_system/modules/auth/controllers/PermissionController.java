package com.shutiye.inventory_system.modules.auth.controllers;

import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PermissionUpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.PermissionService;
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

/**
 * REST controller for permission management operations.
 * Requires authentication for all endpoints.
 */
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permissions", description = "Permission management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
    
    private final PermissionService permissionService;
    
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    
    /**
     * Create a new permission.
     * 
     * @param request Permission creation request
     * @return Created permission DTO
     */
    @PostMapping
    @Operation(summary = "Create permission", description = "Create a new permission")
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        logger.info("Creating permission: {}", request.getCode());
        PermissionDTO permission = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }
    
    /**
     * Update a permission.
     * 
     * @param id Permission ID
     * @param request Permission update request
     * @return Updated permission DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update permission", description = "Update permission information")
    public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Long id,
                                                         @Valid @RequestBody PermissionUpdateRequest request) {
        logger.info("Updating permission with ID: {}", id);
        PermissionDTO permission = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(permission);
    }
    
    /**
     * Delete a permission.
     * 
     * @param id Permission ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission", description = "Delete a permission by ID")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        logger.info("Deleting permission with ID: {}", id);
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all permissions (without pagination - for backward compatibility).
     * 
     * @return List of all permission DTOs
     */
    @GetMapping("/all")
    @Operation(summary = "Get all permissions (no pagination)", description = "Retrieve all permissions in the system without pagination")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        logger.debug("Fetching all permissions");
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }
    
    /**
     * Get permissions with pagination, search, and filtering.
     * 
     * @param pageRequest Pagination, search, and filter parameters
     * @return Paginated response with permission DTOs
     */
    @GetMapping
    @Operation(summary = "Get permissions (paginated)", 
               description = "Retrieve permissions with pagination, search, and filtering. " +
                           "Supports: page, size, search (global), sortBy, sortDir, filters (field-specific)")
    public ResponseEntity<PageResponseDTO<PermissionDTO>> getAllPermissionsPaginated(
            @ModelAttribute PageRequestDTO pageRequest) {
        logger.debug("Fetching permissions with pagination: page={}, size={}, search={}", 
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
        PageResponseDTO<PermissionDTO> response = permissionService.getAllPermissions(pageRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get permission by ID.
     * 
     * @param id Permission ID
     * @return Permission DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID", description = "Retrieve a specific permission by ID")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {
        logger.debug("Fetching permission with ID: {}", id);
        PermissionDTO permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }
}

