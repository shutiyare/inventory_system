package com.shutiye.inventory_system.modules.auth.utils;

import com.shutiye.inventory_system.entity.User;
import com.shutiye.inventory_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SecurityUtils.
 * Tests current user retrieval and permission checking functionality.
 */
@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SecurityUtils securityUtils;

    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .active(true)
                .build();

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("USER_CREATE"));
        authorities.add(new SimpleGrantedAuthority("USER_VIEW"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("password")
                .authorities(authorities)
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetCurrentUser_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = securityUtils.getCurrentUser();

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testGetCurrentUser_NotAuthenticated_ReturnsEmpty() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        Optional<User> result = securityUtils.getCurrentUser();

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetCurrentUsername_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        String username = securityUtils.getCurrentUsername();

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void testHasPermission_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenAnswer(invocation -> userDetails.getAuthorities());

        // Act
        boolean result = securityUtils.hasPermission("USER_CREATE");

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasPermission_NotFound_ReturnsFalse() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenAnswer(invocation -> userDetails.getAuthorities());

        // Act
        boolean result = securityUtils.hasPermission("NONEXISTENT_PERMISSION");

        // Assert
        assertFalse(result);
    }

    @Test
    void testHasRole_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenAnswer(invocation -> userDetails.getAuthorities());

        // Act
        boolean result = securityUtils.hasRole("ADMIN");

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasAnyPermission_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenAnswer(invocation -> userDetails.getAuthorities());

        // Act
        boolean result = securityUtils.hasAnyPermission("USER_CREATE", "NONEXISTENT");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsAuthenticated_True() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        boolean result = securityUtils.isAuthenticated();

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsAuthenticated_False() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean result = securityUtils.isAuthenticated();

        // Assert
        assertFalse(result);
    }
}

