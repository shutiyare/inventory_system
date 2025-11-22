package com.shutiye.inventory_system.modules.auth.controllers;

import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.UserDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserUpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * REST controller for user management operations.
 * Requires authentication for all endpoints.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Create a new user.
     * 
     * @param request User creation request
     * @return Created user DTO
     */
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user account")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateRequest request) {
        logger.info("Creating user: {}", request.getUsername());
        UserDTO user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    /**
     * Get all users (without pagination - for backward compatibility).
     * 
     * @return List of all user DTOs
     */
    @GetMapping("/all")
    @Operation(summary = "Get all users (no pagination)", description = "Retrieve all users in the system without pagination")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.debug("Fetching all users");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get users with pagination, search, and filtering.
     * Supports DataTable-like format with pagination, search, and field filters.
     * 
     * @param pageRequest Pagination, search, and filter parameters
     * @return Paginated response with user DTOs
     */
    @GetMapping
    @Operation(summary = "Get users (paginated)", 
               description = "Retrieve users with pagination, search, and filtering. " +
                           "Supports: page, size, search (global), sortBy, sortDir, filters (field-specific)")
    public ResponseEntity<PageResponseDTO<UserDTO>> getAllUsersPaginated(
            @ModelAttribute PageRequestDTO pageRequest) {
        logger.debug("Fetching users with pagination: page={}, size={}, search={}", 
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
        PageResponseDTO<UserDTO> response = userService.getAllUsers(pageRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get user by ID.
     * 
     * @param id User ID
     * @return User DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.debug("Fetching user with ID: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Update a user.
     * 
     * @param id User ID
     * @param request User update request
     * @return Updated user DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @Valid @RequestBody UserUpdateRequest request) {
        logger.info("Updating user with ID: {}", id);
        UserDTO user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Delete a user.
     * 
     * @param id User ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Assign roles to a user.
     * 
     * @param id User ID
     * @param roleIds Set of role IDs to assign
     * @return Updated user DTO with assigned roles
     */
    @PutMapping("/{id}/assign-roles")
    @Operation(summary = "Assign roles to user", description = "Assign one or more roles to a user")
    public ResponseEntity<UserDTO> assignRoles(@PathVariable Long id, 
                                               @RequestBody Set<Long> roleIds) {
        logger.info("Assigning roles to user with ID: {}", id);
        UserDTO user = userService.assignRoles(id, roleIds);
        return ResponseEntity.ok(user);
    }
}

