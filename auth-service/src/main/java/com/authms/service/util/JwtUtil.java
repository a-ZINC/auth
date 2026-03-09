package com.authms.service.util;

import com.authms.service.config.AuthServiceProperties;
import com.authms.service.entity.User;
import com.authms.service.entity.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final AuthServiceProperties authServiceProperties;

    private SecretKey getSigningKey() {
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                authServiceProperties.getJwt().getSecretKey().getBytes()
        );
    }

    public String generateAccessToken(User user, UserRole role) {
        long now = System.currentTimeMillis();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", role.getRole());
        if (user.getEmail() != null) {
            claims.put("email", user.getEmail());
        }
        if (user.getPhone() != null) {
            claims.put("phone", user.getPhone());
        }

        claims.put("type", "access");

        return Jwts.builder()
                .claims(claims)
                .issuer(authServiceProperties.getJwt().getIssuer())
                .issuedAt(new Date(now))
                .expiration(new Date(now + authServiceProperties.getJwt().getAccessTokenExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        long now = System.currentTimeMillis();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("type", "refresh");

        return Jwts.builder()
                .claims(claims)
                .issuer(authServiceProperties.getJwt().getIssuer())
                .issuedAt(new Date(now))
                .expiration(new Date(now + authServiceProperties.getJwt().getRefreshTokenExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractClaims(token)
                .get("userId", Long.class);
    }

    public String extractRoles(String token) {
        return extractClaims(token)
                .get("roles", String.class);
    }

    public String extractEmail(String token) {
        return extractClaims(token)
                .get("email", String.class);
    }

    public String extractPhone(String token) {
        return extractClaims(token)
                .get("phone", String.class);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isRefreshToken(String token) {
        return extractClaims(token).get("type", String.class).equals("refresh");
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return true;
        } catch(ExpiredJwtException e) {
            log.info("Token expired: {}", e.getMessage());
            return false;
        } catch(UnsupportedJwtException e) {
            log.error("Unsupported token: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Malformed token: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.error("Invalid token signature: {}", e.getMessage());
            return false;
        } catch(Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public TokenValidationResult validateTokenWithReason(String token) {
        if (token == null || token.trim().isBlank()) {
           return TokenValidationResult.fail("TOKEN_MISSING");
        }
        try {
            Claims claims = extractClaims(token);
            String type = claims.get("type", String.class);
            if (!"access".equals(type) && !"refresh".equals(type)) {
                return TokenValidationResult.fail("INVALID_TYPE");
            }

            if ("refresh".equals(type)) {
                return TokenValidationResult.fail("REFRESH_TOKEN_NOT_ACCEPTED");
            }
            return TokenValidationResult.success(claims);
        } catch(ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            return TokenValidationResult.fail("EXPIRED");
        } catch(UnsupportedJwtException e) {
            log.error("Unsupported token: {}", e.getMessage());
            return TokenValidationResult.fail("UNSUPPORTED");
        } catch (MalformedJwtException e) {
            log.error("Malformed token: {}", e.getMessage());
            return TokenValidationResult.fail("MALFORMED");
        } catch (SecurityException e) {
            log.error("Invalid token signature: {}", e.getMessage());
            return TokenValidationResult.fail("SECURITY_EXCEPTION");
        } catch(Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return TokenValidationResult.fail("INVALID");
        }
    }

    public static class TokenValidationResult {

        public final boolean valid;
        public final String failReason;
        public final Claims claims;

        private TokenValidationResult(
                boolean valid,
                String failReason,
                Claims claims
        ) {
            this.valid = valid;
            this.failReason = failReason;
            this.claims = claims;
        }

        public static TokenValidationResult success(Claims claims) {
            return new TokenValidationResult(true, null, claims);
        }

        public static TokenValidationResult fail(String reason) {
            return new TokenValidationResult(false, reason, null);
        }
    }



}
