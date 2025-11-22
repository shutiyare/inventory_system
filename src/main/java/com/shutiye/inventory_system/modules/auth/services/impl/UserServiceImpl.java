package com.shutiye.inventory_system.modules.auth.services.impl;

import com.shutiye.inventory_system.entity.Permission;
import com.shutiye.inventory_system.entity.Role;
import com.shutiye.inventory_system.entity.User;
import com.shutiye.inventory_system.exception.ResourceAlreadyExistsException;
import com.shutiye.inventory_system.exception.ResourceNotFoundException;
import com.shutiye.inventory_system.modules.auth.dtos.PageRequestDTO;
import com.shutiye.inventory_system.modules.auth.dtos.PageResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.UserDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserUpdateRequest;
import com.shutiye.inventory_system.modules.auth.services.UserService;
import com.shutiye.inventory_system.modules.auth.utils.AuditingHelper;
import com.shutiye.inventory_system.modules.auth.utils.PaginationHelper;
import com.shutiye.inventory_system.modules.auth.utils.SpecificationHelper;
import com.shutiye.inventory_system.repository.RoleRepository;
import com.shutiye.inventory_system.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface.
 * Handles user creation, role assignment, and Spring Security integration.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditingHelper auditingHelper;
    
    public UserServiceImpl(UserRepository userRepository, 
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          AuditingHelper auditingHelper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditingHelper = auditingHelper;
    }
    
    @Override
    @CacheEvict(value = {"users", "usersPage"}, allEntries = true)
    public UserDTO createUser(UserCreateRequest request) {
        logger.info("Creating new user with username: {}", request.getUsername());
        
        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Username already exists: {}", request.getUsername());
            throw new ResourceAlreadyExistsException("User", "username", request.getUsername());
        }
        
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email already exists: {}", request.getEmail());
            throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
        }
        
        // Create user entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password using BCrypt
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        
        // Set auditing fields
        auditingHelper.setAuditFields(user);
        
        // Assign roles if provided
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        
        return UserDTO.fromEntity(savedUser);
    }
    
    @Override
    @CacheEvict(value = {"users", "usersPage", "user"}, key = "#id")
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        logger.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
        
        // Update fields if provided
        if (request.getEmail() != null) {
            // Check email uniqueness if changed
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        
        // Update roles if provided
        if (request.getRoleIds() != null) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        
        // Update auditing fields
        auditingHelper.setModifiedAudit(user);
        
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", id);
        
        return UserDTO.fromEntity(updatedUser);
    }
    
    @Override
    @CacheEvict(value = {"users", "usersPage", "user"}, key = "#id", allEntries = true)
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
        
        userRepository.delete(user);
        logger.info("User deleted successfully with ID: {}", id);
    }
    
    @Override
    @CacheEvict(value = {"users", "usersPage", "user"}, key = "#userId")
    public UserDTO assignRoles(Long userId, Set<Long> roleIds) {
        logger.info("Assigning roles to user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });
        
        // Fetch all roles
        Set<Role> roles = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> {
                            logger.warn("Role not found with ID: {}", roleId);
                            return new ResourceNotFoundException("Role", "id", roleId);
                        }))
                .collect(Collectors.toSet());
        
        user.setRoles(roles);
        // Update auditing fields
        auditingHelper.setModifiedAudit(user);
        User updatedUser = userRepository.save(user);
        logger.info("Roles assigned successfully to user with ID: {}", userId);
        
        return UserDTO.fromEntity(updatedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", unless = "#result == null || #result.isEmpty()")
    public List<UserDTO> getAllUsers() {
        logger.debug("Fetching all users (cache miss - fetching from database)");
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<UserDTO> getAllUsers(PageRequestDTO pageRequest) {
        logger.debug("Fetching users with pagination: page={}, size={}, search={}", 
                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSearch());
        
        // Build specifications for search and filters
        Specification<User> searchSpec = SpecificationHelper.buildSearchSpecification(
                pageRequest.getSearch(),
                "username", "email", "fullName"
        );
        
        Specification<User> filterSpec = SpecificationHelper.buildFilterSpecification(
                pageRequest.getFilters()
        );
        
        // Combine specifications
        Specification<User> combinedSpec = SpecificationHelper.and(searchSpec, filterSpec);
        
        // Create pageable
        org.springframework.data.domain.Pageable pageable = PaginationHelper.createPageable(pageRequest);
        
        // Get total count (before filtering)
        long totalRecords = userRepository.count();
        
        // Execute query with pagination
        Page<User> page = combinedSpec != null 
                ? userRepository.findAll(combinedSpec, pageable)
                : userRepository.findAll(pageable);
        
        // Get filtered count
        long filteredRecords = combinedSpec != null 
                ? userRepository.count(combinedSpec)
                : totalRecords;
        
        // Convert to DTOs
        List<UserDTO> userDTOs = page.getContent().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
        
        logger.debug("Fetched {} users (page {} of {})", 
                userDTOs.size(), pageRequest.getPageNumber(), page.getTotalPages());
        
        return PageResponseDTO.of(
                userDTOs,
                totalRecords,
                filteredRecords,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    public UserDTO getUserById(Long id) {
        logger.debug("Fetching user with ID: {} (cache miss - fetching from database)", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
        return UserDTO.fromEntity(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
        
        if (!user.getActive()) {
            logger.warn("User is inactive: {}", username);
            throw new UsernameNotFoundException("User is inactive: " + username);
        }
        
        // Build authorities from user's roles and permissions
        // Extract permissions from all roles
        // Note: Since roles are EAGER loaded but permissions are LAZY, accessing them will trigger loading
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        
        // Initialize roles collection to ensure it's loaded (it's EAGER but being explicit)
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                // Add role name as authority (prefixed with ROLE_)
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
                
                // Initialize and add permissions from role
                // Accessing permissions will trigger LAZY loading within the transaction
                Set<Permission> permissions = role.getPermissions();
                if (permissions != null) {
                    // Force initialization by accessing the collection
                    permissions.size(); // This triggers LAZY loading
                    permissions.forEach(permission -> 
                        authorities.add(new SimpleGrantedAuthority(permission.getCode()))
                    );
                }
            }
        }
        
        logger.debug("User loaded successfully: {} with {} authorities", username, authorities.size());
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }
}

