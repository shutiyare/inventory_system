package com.shutiye.inventory_system.modules.auth.controllers;

import com.shutiye.inventory_system.modules.auth.dtos.LoginResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.UserLoginRequest;
import com.shutiye.inventory_system.modules.auth.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Handles user login and registration endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * User login endpoint.
     * Authenticates user and returns JWT token.
     * 
     * @param request Login request containing username and password
     * @return Login response with JWT token and user information
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and get access token and refresh token")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody UserLoginRequest request) {
        logger.info("Login request received for user: {}", request.getUsername());
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * User registration endpoint.
     * Creates a new user account and returns JWT token.
     * 
     * @param request Registration request containing user details
     * @return Login response with access token, refresh token, and user information
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user and get access token and refresh token")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody UserCreateRequest request) {
        logger.info("Registration request received for user: {}", request.getUsername());
        LoginResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

