package com.naarith.starter.features.auth.controller;

import com.naarith.starter.features.auth.dto.LoginReqDTO;
import com.naarith.starter.features.auth.dto.LoginResDTO;
import com.naarith.starter.features.auth.dto.RegisterReqDTO;
import com.naarith.starter.features.auth.exception.TokenInvalidException;
import com.naarith.starter.features.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    /// =========================
    /// Register
    /// =========================
    @Operation(summary = "Register")
    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "409", description = "Email already exists."),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation error.")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterReqDTO reqDTO) {
        log.info("Register attempt for email={}", reqDTO.email());
        service.register(reqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /// =========================
    /// Login
    /// =========================
    @Operation(summary = "Login")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation error."),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResDTO> login(@RequestBody @Valid LoginReqDTO reqDTO) {
        log.info("Login attempt for email={}", reqDTO.email());
        var loginResult = service.login(reqDTO);

        /// Set refresh token cookie
        var cookie = ResponseCookie.from("refresh_token", loginResult.refreshToken())
                .httpOnly(true)
                .secure(true) /// Set as Secure if using HTTPS
                .path("/")
                .maxAge(Duration.ofSeconds(3900)) /// 65min
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResult.resDTO());
    }

    /// =========================
    /// Refresh token
    /// =========================
    @Operation(summary = "Refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @GetMapping("/refresh")
    public ResponseEntity<LoginResDTO> refresh(HttpServletRequest request) {
        log.info("Received refresh token request");
        var cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (var cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            log.warn("No refresh token found in cookie");
            throw new TokenInvalidException();
        }

        var result = service.refreshToken(refreshToken);

        /// Set refresh token cookie
        var cookie = ResponseCookie.from("refresh_token", result.refreshToken())
                .httpOnly(true)
                .secure(true) /// Set as Secure if using HTTPS
                .path("/")
                .maxAge(Duration.ofSeconds(3900)) /// 65min
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.resDTO());
    }
}
