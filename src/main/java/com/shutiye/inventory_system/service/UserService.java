package com.shutiye.inventory_system.service;

import com.shutiye.inventory_system.dto.UserRequest;
import com.shutiye.inventory_system.dto.UserResponse;
import com.shutiye.inventory_system.entity.User;
import com.shutiye.inventory_system.exception.ResourceAlreadyExistsException;
import com.shutiye.inventory_system.exception.ResourceNotFoundException;
import com.shutiye.inventory_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for User operations
 * Implements clean code principles with proper logging and transaction management
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user
     */
    public UserResponse createUser(UserRequest userRequest) {
        logger.info("Creating new user with username: {}", userRequest.username());
        
        // Check if username already exists
        if (userRepository.existsByUsername(userRequest.username())) {
            logger.warn("Username already exists: {}", userRequest.username());
            throw new ResourceAlreadyExistsException("User", "username", userRequest.username());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.email())) {
            logger.warn("Email already exists: {}", userRequest.email());
            throw new ResourceAlreadyExistsException("User", "email", userRequest.email());
        }
        
        User user = User.builder()
                .username(userRequest.username())
                .email(userRequest.email())
                .firstName(userRequest.firstName())
                .lastName(userRequest.lastName())
                .password(userRequest.password()) // In production, this should be hashed
                .phoneNumber(userRequest.phoneNumber())
                .active(userRequest.active() != null ? userRequest.active() : true)
                .build();
        
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        
        return mapToResponse(savedUser);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
        
        return mapToResponse(user);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        logger.debug("Fetching all users");
        
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Update user by ID
     */
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        logger.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
        
        // Check if username is being changed and if it already exists
        if (!user.getUsername().equals(userRequest.username()) && 
            userRepository.existsByUsername(userRequest.username())) {
            logger.warn("Username already exists: {}", userRequest.username());
            throw new ResourceAlreadyExistsException("User", "username", userRequest.username());
        }
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userRequest.email()) && 
            userRepository.existsByEmail(userRequest.email())) {
            logger.warn("Email already exists: {}", userRequest.email());
            throw new ResourceAlreadyExistsException("User", "email", userRequest.email());
        }
        
        user.setUsername(userRequest.username());
        user.setEmail(userRequest.email());
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());
        user.setPassword(userRequest.password()); // In production, this should be hashed
        user.setPhoneNumber(userRequest.phoneNumber());
        if (userRequest.active() != null) {
            user.setActive(userRequest.active());
        }
        
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        
        return mapToResponse(updatedUser);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            logger.warn("User not found with ID: {}", id);
            throw new ResourceNotFoundException("User", "id", id);
        }
        
        userRepository.deleteById(id);
        logger.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Map User entity to UserResponse DTO
     */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

