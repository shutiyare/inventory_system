package com.shutiye.inventory_system.modules.auth.services.impl;

import com.shutiye.inventory_system.entity.User;
import com.shutiye.inventory_system.modules.auth.dtos.LoginResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.UserDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserLoginRequest;
import com.shutiye.inventory_system.modules.auth.services.AuthService;
import com.shutiye.inventory_system.modules.auth.services.UserService;
import com.shutiye.inventory_system.modules.auth.utils.JwtTokenProvider;
import com.shutiye.inventory_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService interface.
 * Handles user authentication, registration, and JWT token generation.
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public LoginResponseDTO login(UserLoginRequest request) {
        logger.info("Attempting login for user: {}", request.getUsername());
        
        try {
            // Authenticate user using Spring Security
            // This will validate credentials and throw exception if invalid
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate access and refresh tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            // Get user details by username
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            
            // Load user entity to get full user info
            User userEntity = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));
            
            // Build simplified user info
            LoginResponseDTO.UserInfo userInfo = LoginResponseDTO.UserInfo.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .fullName(userEntity.getFullName())
                    .email(userEntity.getEmail())
                    .role_ids(userEntity.getRoles() != null ? 
                        userEntity.getRoles().stream()
                            .map(role -> role.getId())
                            .toList() : java.util.Collections.emptyList())
                    .active(userEntity.getActive())
                    .build();
            
            logger.info("Login successful for user: {}", request.getUsername());
            
            return LoginResponseDTO.builder()
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .user(userInfo)
                    .build();
                    
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for user: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
    }
    
    @Override
    public LoginResponseDTO register(UserCreateRequest request) {
        logger.info("Registering new user: {}", request.getUsername());
        
        // Create user using UserService
        UserDTO userDTO = userService.createUser(request);
        
        // Automatically authenticate the newly registered user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate access and refresh tokens
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        // Build simplified user info
        LoginResponseDTO.UserInfo userInfo = LoginResponseDTO.UserInfo.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .role_ids(userDTO.getRoles() != null ? 
                    userDTO.getRoles().stream()
                        .map(role -> role.getId())
                        .toList() : java.util.Collections.emptyList())
                .active(userDTO.getActive())
                .build();
        
        logger.info("Registration successful for user: {}", request.getUsername());
        
        return LoginResponseDTO.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .user(userInfo)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateCredentials(String username, String password) {
        logger.debug("Validating credentials for user: {}", username);
        
        try {
            // Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            return true;
        } catch (BadCredentialsException e) {
            logger.debug("Invalid credentials for user: {}", username);
            return false;
        }
    }
}

