package com.naarith.starter.features.auth.security;

import com.naarith.starter.exception.ApiError;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    /// Define security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        /// Public endpoints
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        /// Open api docs
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        /// All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(customizer -> customizer
                        /// Configure handler for 401 Unauthorized errors
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        /// Configure handler for 403 Forbidden errors
                        .accessDeniedHandler(customAccessDeniedHandler())
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /// Define PasswordEncoder bean - make it injectable
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /// Custom AuthenticationProvider
    /// If you don’t need special tweaks (like setHideUserNotFoundExceptions(false)), you can remove the bean
    /// Then Spring Boot will autoconfigure a DaoAuthenticationProvider using your CustomUserDetailsService and PasswordEncoder.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        /// When set to false, AuthenticationManager.authenticate() will throw
        /// UsernameNotFoundException if the user is not found.
        ///
        /// By default, (true), AuthenticationManager.authenticate() will throw
        /// BadCredentialsException for both "user not found" and "incorrect password".
        provider.setHideUserNotFoundExceptions(false);

        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /// Define AuthenticationManager bean - make it injectable
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    /// Custom AuthenticationEntryPoint implementation
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(ApiError.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Unauthorized")
                    .build().toJson());
        };
    }

    /// Custom AccessDeniedHandler implementation
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(ApiError.builder()
                    .status(HttpStatus.FORBIDDEN.value())
                    .message("Forbidden")
                    .build().toJson());
        };
    }
}
