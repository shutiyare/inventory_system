package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.UserDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserUpdateRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

/**
 * Service interface for User operations.
 * Extends UserDetailsService for Spring Security integration.
 */
public interface UserService extends UserDetailsService {
    
    /**
     * Create a new user with the provided information.
     * Password will be hashed before storage.
     * 
     * @param request User creation request containing user details
     * @return Created user DTO without password
     */
    UserDTO createUser(UserCreateRequest request);
    
    /**
     * Update an existing user.
     * 
     * @param id User ID
     * @param request User update request
     * @return Updated user DTO
     */
    UserDTO updateUser(Long id, UserUpdateRequest request);
    
    /**
     * Delete a user by ID.
     * 
     * @param id User ID
     */
    void deleteUser(Long id);
    
    /**
     * Assign roles to a user by user ID and role IDs.
     * 
     * @param userId ID of the user
     * @param roleIds Set of role IDs to assign
     * @return Updated user DTO with assigned roles
     */
    UserDTO assignRoles(Long userId, Set<Long> roleIds);
    
    /**
     * Get all users in the system (without pagination).
     * 
     * @return List of all user DTOs
     */
    List<UserDTO> getAllUsers();
    
    /**
     * Get users with pagination, search, and filtering.
     * 
     * @param pageRequest Pagination, search, and filter request
     * @return Paginated response with user DTOs
     */
    PageResponseDTO<UserDTO> getAllUsers(PageRequestDTO pageRequest);
    
    /**
     * Get a user by ID.
     * 
     * @param id User ID
     * @return User DTO if found
     */
    UserDTO getUserById(Long id);
    
    /**
     * Load user by username for Spring Security authentication.
     * This method is required by UserDetailsService interface.
     * 
     * @param username Username to load
     * @return UserDetails object for Spring Security
     */
    @Override
    org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username);
}

