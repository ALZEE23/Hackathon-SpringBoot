package com.niagapulse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT Service Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private String testSecret;
    private long testExpiration;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Setup test secret and expiration - must be valid Base64
        testSecret = "aW52YWxpZEJhc2U2NFN0cmluZ1RoYXRJc1RvbyBMb25nQW5kQ2Fubm90QmVQYXJzZWRQcm9wZXJseQ==";
        testExpiration = 1000 * 60 * 60 * 10; // 10 hours

        // Inject the secret and expiration using reflection
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
    }

    @Test
    @DisplayName("Generate Token - Success with username")
    void testGenerateToken_Success() {
        // Act
        String token = jwtService.generateToken("testuser");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Generate Token - Creates valid JWT format")
    void testGenerateToken_ValidJwtFormat() {
        // Act
        String token = jwtService.generateToken("testuser");

        // Assert - JWT should have 3 parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts (header.payload.signature)");
    }

    @Test
    @DisplayName("Extract Username - Returns correct username")
    void testExtractUsername_Success() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Validate Token - Returns true for matching username")
    void testValidateToken_WithMatchingUsername() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        Boolean isValid = jwtService.validateToken(token, username);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Validate Token - Returns false for mismatched username")
    void testValidateToken_WithInvalidUsername() {
        // Arrange
        String username = "testuser";
        String wrongUsername = "wronguser";
        String token = jwtService.generateToken(username);

        // Act
        Boolean isValid = jwtService.validateToken(token, wrongUsername);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Extract Expiration - Returns future Date")
    void testExtractExpiration_ReturnsFutureDate() {
        // Arrange
        String token = jwtService.generateToken("testuser");

        // Act
        Date expiration = jwtService.extractExpiration(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.getTime() > System.currentTimeMillis());
    }

    @Test
    @DisplayName("Generate Token with Claims - Includes username")
    void testGenerateToken_WithClaims() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");

        // Act
        String token = jwtService.generateToken(claims, "testuser");

        // Assert
        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("Multiple Tokens - Can generate multiple tokens")
    void testMultipleTokens_AreUnique() {
        // Act
        String token1 = jwtService.generateToken("testuser");
        String token2 = jwtService.generateToken("anotheruser");

        // Assert - Different users get different tokens
        assertNotEquals(token1, token2);
    }
}
