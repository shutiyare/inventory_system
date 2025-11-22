package com.shutiye.inventory_system.modules.auth.services;

import com.shutiye.inventory_system.entity.User;
import com.shutiye.inventory_system.modules.auth.dtos.LoginResponseDTO;
import com.shutiye.inventory_system.modules.auth.dtos.UserCreateRequest;
import com.shutiye.inventory_system.modules.auth.dtos.UserLoginRequest;
import com.shutiye.inventory_system.modules.auth.services.impl.AuthServiceImpl;
import com.shutiye.inventory_system.modules.auth.utils.JwtTokenProvider;
import com.shutiye.inventory_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 * Tests login, registration, and credential validation functionality.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserLoginRequest loginRequest;
    private UserCreateRequest registerRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .password("encodedPassword")
                .active(true)
                .build();

        loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = UserCreateRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build();

        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void testLogin_Success() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class))).thenReturn("refresh-token");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        LoginResponseDTO response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccess_token());
        assertEquals("refresh-token", response.getRefresh_token());
        assertNotNull(response.getUser());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(any(Authentication.class));
    }

    @Test
    void testLogin_InvalidCredentials_ThrowsException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void testRegister_Success() {
        // Arrange
        com.shutiye.inventory_system.modules.auth.dtos.UserDTO userDTO = com.shutiye.inventory_system.modules.auth.dtos.UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .build();

        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(userDTO);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class))).thenReturn("refresh-token");

        // Act
        LoginResponseDTO response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccess_token());
        assertEquals("refresh-token", response.getRefresh_token());
        verify(userService, times(1)).createUser(any(UserCreateRequest.class));
    }

    @Test
    void testValidateCredentials_Valid_ReturnsTrue() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Act
        boolean result = authService.validateCredentials("testuser", "password123");

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateCredentials_Invalid_ReturnsFalse() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        boolean result = authService.validateCredentials("testuser", "wrongpassword");

        // Assert
        assertFalse(result);
    }
}

