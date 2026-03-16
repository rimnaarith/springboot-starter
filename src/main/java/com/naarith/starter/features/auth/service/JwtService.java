package com.naarith.starter.features.auth.service;

import com.naarith.starter.features.auth.enums.TokenType;
import com.naarith.starter.features.auth.exception.TokenExpiredException;
import com.naarith.starter.features.auth.exception.TokenInvalidException;
import com.naarith.starter.features.auth.security.UserPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {
    private final SecretKey secretKey;
    public JwtService(
            @Value("${application.security.secret}")
            String secret
    ) {
        /// Create secret key for secret string
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate Access/Refresh token
     * @param userPrincipal logged in user
     * @param tokenType Access/Refresh
     * @param expiredInSec Expiration time in seconds
     * @return Jwt string
     */
    private String generateToken(UserPrincipal userPrincipal, TokenType tokenType, int expiredInSec) {

        /// Create JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userPrincipal.userDTO().email());
        claims.put("tokenType", tokenType.name());

        /// Generate JWT
        return Jwts.builder()
                .subject(userPrincipal.userDTO().uid())
                .claims().add(claims)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(expiredInSec)))
                .and()
                .signWith(secretKey)
                .compact();
    }

    /**
     * Generate access token for a user
     * @param userPrincipal User
     * @return Jwt string
     */
    public String generateAccessToken(UserPrincipal userPrincipal) {
        log.info("Generate a access token for email={}", userPrincipal.getUsername());
        return generateToken(userPrincipal, TokenType.ACCESS, 600); // 10min
    }

    /**
     * Generate refresh token for a user
     * @param userPrincipal User
     * @return Jwt string
     */
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        log.info("Generate a refresh token for email={}", userPrincipal.getUsername());
        return generateToken(userPrincipal, TokenType.REFRESH, 3600); // 60min
    }

    /**
     * Validate Access/Refresh token
     * @param token Token to validate
     * @param tokenType Access/Refresh
     * @return UserPrincipal
     * @throws TokenExpiredException When the token expires
     * @throws TokenInvalidException When the token is invalid
     */
    private UserPrincipal validateToken(String token, TokenType tokenType) throws TokenExpiredException, TokenInvalidException {
        /// Validate token
        try {
            var claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            /// Validate token type
            var tokenTypeStr = claims.getPayload().get("tokenType", String.class);
            var jwtTokenType = TokenType.valueOf(tokenTypeStr);
            if (!tokenType.equals(jwtTokenType)) {
                /// Invalid token
                throw new TokenInvalidException();
            }

            /// Create UserPrincipal from jwt
            var uid = claims.getPayload().getSubject();
            var email = claims.getPayload().get("email", String.class);
            return UserPrincipal.of(uid, email);

        } catch (ExpiredJwtException e) {
            /// Token expired
            throw new TokenExpiredException();
        } catch (JwtException e) {
            /// Invalid token
            throw new TokenInvalidException();
        }
    }

    /**
     * Validate Access token
     * @param token Token to validate
     * @return UserPrincipal
     * @throws TokenExpiredException When the token expires
     * @throws TokenInvalidException When the token is invalid
     */
    public UserPrincipal validateAccessToken(String token) throws TokenExpiredException, TokenInvalidException {
        return validateToken(token, TokenType.ACCESS);
    }

    /**
     * Validate Refresh token
     * @param token Token to validate
     * @return UserPrincipal
     * @throws TokenExpiredException When the token expires
     * @throws TokenInvalidException When the token is invalid
     */
    public UserPrincipal validateRefreshToken(String token) throws TokenExpiredException, TokenInvalidException {
        return validateToken(token, TokenType.REFRESH);
    }
}
