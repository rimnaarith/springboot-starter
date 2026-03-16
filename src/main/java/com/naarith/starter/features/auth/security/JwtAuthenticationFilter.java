package com.naarith.starter.features.auth.security;

import com.naarith.starter.features.auth.exception.TokenExpiredException;
import com.naarith.starter.features.auth.exception.TokenInvalidException;
import com.naarith.starter.features.auth.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        /// Extract the Authorization header from the request.
        final var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        /// Extract the JWT token by removing the "Bearer " prefix
        final var jwtToken = authHeader.substring(7);

        /// Check if the current request already has an authenticated user in the SecurityContext.
        /// If authentication already exists, we skip token validation to avoid unnecessary processing.
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserPrincipal userPrincipal;
            try {
                /// Validate the access token and retrieve the authenticated user principal
                userPrincipal = jwtService.validateAccessToken(jwtToken);
                log.debug("JWT authenticated user: {}", userPrincipal.getUsername());
            } catch (JwtException | TokenExpiredException | TokenInvalidException e) {
                filterChain.doFilter(request, response);
                return;
            }

            /// Create a new SecurityContext for this request.
            var securityContext = SecurityContextHolder.createEmptyContext();

            /// Create an Authentication object representing the authenticated user.
            var auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            /// Attach request-specific details to the Authentication object.
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            /// Store the Authentication in the SecurityContext so that Spring Security recognizes the request as authenticated.
            securityContext.setAuthentication(auth);
            SecurityContextHolder.setContext(securityContext);
        }

        /// Continue processing the remaining filters in the chain.
        filterChain.doFilter(request, response);
    }
}
