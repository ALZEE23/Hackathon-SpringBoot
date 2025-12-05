package com.niagapulse.controller;

import com.niagapulse.dto.LoginRequest;
import com.niagapulse.dto.RegisterRequest;
import com.niagapulse.model.User;
import com.niagapulse.service.JwtService;
import com.niagapulse.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Controller Tests")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        // Setup test data
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword123");
        testUser.setEmail("test@example.com");

        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTUxNjIzOTAyMn0.RG9nZS1SdWxlcw";
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("Register - Success with valid data")
    void testRegister_Success() {
        // Arrange
        when(userService.register(any(RegisterRequest.class))).thenReturn(testUser);
        when(jwtService.generateToken("testuser")).thenReturn(testToken);

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        verify(userService).register(any(RegisterRequest.class));
        verify(jwtService).generateToken("testuser");
    }

    @Test
    @DisplayName("Register - Returns token for new user")
    void testRegister_ReturnsToken() {
        // Arrange
        when(userService.register(any(RegisterRequest.class))).thenReturn(testUser);
        when(jwtService.generateToken("testuser")).thenReturn(testToken);

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        verify(jwtService).generateToken("testuser");
    }

    @Test
    @DisplayName("Register - Multiple registrations with different usernames")
    void testRegister_MultipleUsers() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setPassword("encodedPass456");

        RegisterRequest anotherRequest = new RegisterRequest();
        anotherRequest.setUsername("anotheruser");
        anotherRequest.setPassword("pass456");

        String anotherToken = "anotherTokenValue123";

        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(testUser)
                .thenReturn(anotherUser);
        when(jwtService.generateToken("testuser")).thenReturn(testToken);
        when(jwtService.generateToken("anotheruser")).thenReturn(anotherToken);

        // Act - First registration
        ResponseEntity<?> response1 = authController.register(registerRequest);
        ResponseEntity<?> response2 = authController.register(anotherRequest);

        // Assert
        assertTrue(response1.getStatusCode().is2xxSuccessful());
        assertTrue(response2.getStatusCode().is2xxSuccessful());
        verify(userService, times(2)).register(any(RegisterRequest.class));
        verify(jwtService, times(2)).generateToken(anyString());
    }

    @Test
    @DisplayName("Register - User service is called with correct request")
    void testRegister_UserServiceCalled() {
        // Arrange
        when(userService.register(any(RegisterRequest.class))).thenReturn(testUser);
        when(jwtService.generateToken(anyString())).thenReturn(testToken);

        // Act
        authController.register(registerRequest);

        // Assert
        verify(userService).register(registerRequest);
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("Login - Success with correct credentials")
    void testLogin_SuccessWithValidCredentials() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn(testToken);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        verify(userService).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword123");
        verify(jwtService).generateToken("testuser");
    }

    @Test
    @DisplayName("Login - Failure with incorrect password")
    void testLogin_FailureWithInvalidPassword() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword123")).thenReturn(false);

        LoginRequest wrongPasswordRequest = new LoginRequest();
        wrongPasswordRequest.setUsername("testuser");
        wrongPasswordRequest.setPassword("wrongpassword");

        // Act
        ResponseEntity<?> response = authController.login(wrongPasswordRequest);

        // Assert
        assertEquals(401, response.getStatusCode().value());
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword123");
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Login - Correct credentials return token")
    void testLogin_TokenIsReturned() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn(testToken);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        verify(jwtService).generateToken("testuser");
    }

    @Test
    @DisplayName("Login - Multiple users with different credentials")
    void testLogin_DifferentUsers() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setPassword("encodedPass456");

        String anotherToken = "anotherTokenValue123";
        LoginRequest anotherLoginRequest = new LoginRequest();
        anotherLoginRequest.setUsername("anotheruser");
        anotherLoginRequest.setPassword("pass456");

        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("anotheruser")).thenReturn(anotherUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(passwordEncoder.matches("pass456", "encodedPass456")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn(testToken);
        when(jwtService.generateToken("anotheruser")).thenReturn(anotherToken);

        // Act - First login
        ResponseEntity<?> response1 = authController.login(loginRequest);
        ResponseEntity<?> response2 = authController.login(anotherLoginRequest);

        // Assert
        assertTrue(response1.getStatusCode().is2xxSuccessful());
        assertTrue(response2.getStatusCode().is2xxSuccessful());
        verify(jwtService).generateToken("testuser");
        verify(jwtService).generateToken("anotheruser");
    }

    @Test
    @DisplayName("Login - Invalid password returns 401 Unauthorized")
    void testLogin_InvalidPasswordReturnsUnauthorized() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword123")).thenReturn(false);

        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setUsername("testuser");
        wrongRequest.setPassword("wrongpassword");

        // Act
        ResponseEntity<?> response = authController.login(wrongRequest);

        // Assert
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Login - Correct password generates token")
    void testLogin_CorrectPasswordGeneratesToken() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn(testToken);

        // Act
        authController.login(loginRequest);

        // Assert
        verify(jwtService).generateToken("testuser");
    }
}
