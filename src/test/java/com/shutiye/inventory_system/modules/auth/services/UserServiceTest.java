package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.entity.Role;
import com.shutiye.inventory_system.entity.User;
import com.shutiye.inventory_system.exception.ResourceAlreadyExistsException;
import com.shutiye.inventory_system.exception.ResourceNotFoundException;
import com.shutiye.inventory_system.modules.auth.dtos.UserCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.UserDTO;
import com.shutiye.inventory_system.modules.auth.services.impl.UserServiceImpl;
import com.shutiye.inventory_system.repository.RoleRepository;
import com.shutiye.inventory_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Tests user creation, role assignment, and user loading functionality.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.shutiye.inventory_system.modules.auth.utils.AuditingHelper auditingHelper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role testRole;
    private UserCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .description("Administrator role")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .password("encodedPassword")
                .active(true)
                .roles(new HashSet<>())
                .build();

        createRequest = UserCreateRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .active(true)
                .build();
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.createUser(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void testCreateUser_UsernameExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.createUser(createRequest);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.createUser(createRequest);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAssignRoles_Success() {
        // Arrange
        Set<Long> roleIds = Set.of(1L);
        testUser.getRoles().add(testRole);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.assignRoles(1L, roleIds);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAssignRoles_UserNotFound_ThrowsException() {
        // Arrange
        Set<Long> roleIds = Set.of(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.assignRoles(1L, roleIds);
        });
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        testUser.getRoles().add(testRole);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        var userDetails = userService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("testuser");
        });
    }

    @Test
    void testLoadUserByUsername_Inactive_ThrowsException() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("testuser");
        });
    }
}

