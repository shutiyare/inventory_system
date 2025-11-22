package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.modules.auth.dtos.LoginResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.UserLoginRequest;

/**
 * Service interface for Authentication operations.
 * Handles user login, registration, and JWT token generation.
 */
public interface AuthService {
    
    /**
     * Authenticate user and generate JWT token.
     * 
     * @param request Login request containing username and password
     * @return Login response with access token, refresh token, and user information
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    LoginResponseDTO login(UserLoginRequest request);
    
    /**
     * Register a new user.
     * 
     * @param request User registration request
     * @return Login response with access token, refresh token, and user information
     * @throws com.shutiye.inventory_system.exception.ResourceAlreadyExistsException if username or email already exists
     */
    LoginResponseDTO register(UserCreateRequest request);
    
    /**
     * Validate user credentials.
     * 
     * @param username Username
     * @param password Plain text password
     * @return true if credentials are valid, false otherwise
     */
    boolean validateCredentials(String username, String password);
}

