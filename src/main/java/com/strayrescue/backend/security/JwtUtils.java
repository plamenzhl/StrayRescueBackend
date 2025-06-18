package com.strayrescue.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    
    @Value("${app.jwtSecret:#{environment.APP_JWT_SECRET}}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:14400000}")  // 4 hours default
    private int jwtExpirationMs;

    private SecretKey signingKey;

    @PostConstruct
    public void validateConfiguration() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalStateException(
                "JWT secret is not configured. Please set APP_JWT_SECRET environment variable."
            );
        }
        
        if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                "JWT secret must be at least 32 characters (256 bits) for HS256 algorithm. " +
                "Current key length: " + jwtSecret.getBytes(StandardCharsets.UTF_8).length + " bytes. " +
                "Please generate a secure key using: openssl rand -base64 32"
            );
        }
        
        // Initialize the signing key once during startup
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        System.out.println("✅ JWT configuration validated successfully");
        System.out.println("📊 JWT expiration time: " + (jwtExpirationMs / 1000 / 60) + " minutes");
    }

    private SecretKey getSigningKey() {
        return this.signingKey;
    }

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("❌ Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("❌ JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("❌ JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ JWT claims string is empty: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ JWT validation error: " + e.getMessage());
        }
        return false;
    }
}