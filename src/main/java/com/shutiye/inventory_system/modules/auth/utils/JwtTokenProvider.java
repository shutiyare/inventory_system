package com.shutiye.inventory_system.modules.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and extraction of claims.
 */
@Component
public class JwtTokenProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    @Value("${jwt.secret:MySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS512Algorithm}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}") // Default 24 hours in milliseconds
    private Long jwtExpiration;
    
    @Value("${jwt.refresh-expiration:604800000}") // Default 7 days in milliseconds
    private Long jwtRefreshExpiration;
    
    /**
     * Generate JWT token from authentication object.
     * Token includes username and authorities (roles and permissions).
     * 
     * @param authentication Spring Security authentication object
     * @return JWT token string
     */
    public String generateToken(Authentication authentication) {
        logger.debug("Generating JWT token for user: {}", authentication.getName());
        
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        String token = Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
        
        logger.debug("JWT token generated successfully for user: {}", username);
        return token;
    }
    
    /**
     * Get username from JWT token.
     * 
     * @param token JWT token string
     * @return Username extracted from token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    /**
     * Get authorities from JWT token.
     * 
     * @param token JWT token string
     * @return Comma-separated authorities string
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("authorities", String.class);
    }
    
    /**
     * Validate JWT token.
     * Checks if token is not expired and signature is valid.
     * 
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date());
        } catch (Exception e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract claims from JWT token.
     * 
     * @param token JWT token string
     * @return Claims object containing token data
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Generate refresh token from authentication object.
     * Refresh tokens have longer expiration time.
     * 
     * @param authentication Spring Security authentication object
     * @return JWT refresh token string
     */
    public String generateRefreshToken(Authentication authentication) {
        logger.debug("Generating refresh token for user: {}", authentication.getName());
        
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        String token = Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
        
        logger.debug("Refresh token generated successfully for user: {}", username);
        return token;
    }
    
    /**
     * Get token expiration time in milliseconds.
     * 
     * @return Expiration time in milliseconds
     */
    public Long getExpirationTime() {
        return jwtExpiration;
    }
    
    /**
     * Get refresh token expiration time in milliseconds.
     * 
     * @return Refresh token expiration time in milliseconds
     */
    public Long getRefreshExpirationTime() {
        return jwtRefreshExpiration;
    }
}

