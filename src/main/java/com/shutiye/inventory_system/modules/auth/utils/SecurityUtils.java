package com.shutiye.inventory_system.modules.auth.utils;

import com.shutiye.inventory_system.entity.User;
import com.shutiye.inventory_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Security utility class for accessing current user and checking permissions.
 * Provides helper methods similar to FastAPI's get_session and @permission_checker.
 * 
 * Usage examples:
 * - SecurityUtils.getCurrentUser() - Get current authenticated user entity
 * - SecurityUtils.getCurrentUsername() - Get current username
 * - SecurityUtils.hasPermission("USER_CREATE") - Check if user has permission
 * - SecurityUtils.hasAnyPermission("USER_CREATE", "USER_UPDATE") - Check if user has any permission
 * - SecurityUtils.hasRole("ADMIN") - Check if user has role
 */
@Component
public class SecurityUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);
    
    private final UserRepository userRepository;
    
    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Get current authenticated user entity.
     * Similar to FastAPI's get_session() which returns current user data.
     * 
     * @return Optional containing current User entity, or empty if not authenticated
     */
    public Optional<User> getCurrentUser() {
        try {
            String username = getCurrentUsername();
            if (username != null) {
                return userRepository.findByUsername(username);
            }
        } catch (Exception e) {
            logger.debug("Error getting current user: {}", e.getMessage());
        }
        return Optional.empty();
    }
    
    /**
     * Get current authenticated user entity (non-optional version).
     * Throws exception if user is not authenticated.
     * 
     * @return Current User entity
     * @throws IllegalStateException if user is not authenticated
     */
    public User getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
    
    /**
     * Get current authenticated username.
     * 
     * @return Username of current user, or null if not authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
    
    /**
     * Get current user ID.
     * 
     * @return ID of current user, or null if not authenticated
     */
    public Long getCurrentUserId() {
        return getCurrentUser()
                .map(User::getId)
                .orElse(null);
    }
    
    /**
     * Get all authorities (permissions and roles) of current user.
     * 
     * @return Collection of authorities
     */
    public Collection<? extends GrantedAuthority> getCurrentAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities();
        }
        return java.util.Collections.emptyList();
    }
    
    /**
     * Check if current user has a specific permission.
     * Similar to FastAPI's @permission_checker decorator.
     * 
     * @param permissionCode Permission code to check (e.g., "USER_CREATE")
     * @return true if user has the permission, false otherwise
     */
    public boolean hasPermission(String permissionCode) {
        Collection<? extends GrantedAuthority> authorities = getCurrentAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(permissionCode));
    }
    
    /**
     * Check if current user has any of the specified permissions.
     * 
     * @param permissionCodes Permission codes to check
     * @return true if user has at least one of the permissions, false otherwise
     */
    public boolean hasAnyPermission(String... permissionCodes) {
        Collection<? extends GrantedAuthority> authorities = getCurrentAuthorities();
        Collection<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        for (String code : permissionCodes) {
            if (authorityStrings.contains(code)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if current user has all of the specified permissions.
     * 
     * @param permissionCodes Permission codes to check
     * @return true if user has all permissions, false otherwise
     */
    public boolean hasAllPermissions(String... permissionCodes) {
        Collection<? extends GrantedAuthority> authorities = getCurrentAuthorities();
        Collection<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        for (String code : permissionCodes) {
            if (!authorityStrings.contains(code)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if current user has a specific role.
     * 
     * @param roleName Role name to check (e.g., "ADMIN", "SUPER_ADMIN")
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        Collection<? extends GrantedAuthority> authorities = getCurrentAuthorities();
        String roleAuthority = "ROLE_" + roleName.toUpperCase();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(roleAuthority));
    }
    
    /**
     * Check if current user has any of the specified roles.
     * 
     * @param roleNames Role names to check
     * @return true if user has at least one of the roles, false otherwise
     */
    public boolean hasAnyRole(String... roleNames) {
        Collection<? extends GrantedAuthority> authorities = getCurrentAuthorities();
        Collection<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        for (String roleName : roleNames) {
            String roleAuthority = "ROLE_" + roleName.toUpperCase();
            if (authorityStrings.contains(roleAuthority)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if current user is authenticated.
     * 
     * @return true if user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
                && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}

