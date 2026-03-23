package com.example.demo.security;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtUtil — token generation and validation")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject values normally set by @Value
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "test-secret-key-must-be-at-least-256-bits-long-to-pass-hmac-sha256-check");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);         // 1 day
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 604800000L); // 7 days

        User user = new User();
        user.setId(42L);
        user.setEmail("test@example.com");
        user.setPassword("hashed");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);

        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("generateAccessToken → valid token, correct email extracted")
    void generateAndExtractEmail() {
        String token = jwtUtil.generateAccessToken(userDetails);
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("generateRefreshToken → valid, different from access token")
    void generateRefreshTokenIsDifferent() {
        String access = jwtUtil.generateAccessToken(userDetails);
        String refresh = jwtUtil.generateRefreshToken(userDetails);
        assertThat(refresh).isNotBlank();
        assertThat(refresh).isNotEqualTo(access);
    }

    @Test
    @DisplayName("isTokenValid → true for freshly generated token")
    void freshTokenIsValid() {
        String token = jwtUtil.generateAccessToken(userDetails);
        assertThat(jwtUtil.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid → false for expired token")
    void expiredTokenIsInvalid() {
        // Generate token that expired 1ms ago
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L);
        String token = jwtUtil.generateAccessToken(userDetails);

        // Restore normal expiration for validation
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        assertThat(jwtUtil.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid → false for tampered token")
    void tamperedTokenIsInvalid() {
        String token = jwtUtil.generateAccessToken(userDetails);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(jwtUtil.isTokenValid(tampered, userDetails)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid → false when email doesn't match")
    void mismatchedEmailIsInvalid() {
        String token = jwtUtil.generateAccessToken(userDetails);

        // Create different user with different email
        User otherUser = new User();
        otherUser.setId(99L);
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("hashed");
        otherUser.setRole(UserRole.CUSTOMER);
        otherUser.setActive(true);
        CustomUserDetails otherDetails = new CustomUserDetails(otherUser);

        assertThat(jwtUtil.isTokenValid(token, otherDetails)).isFalse();
    }
}
