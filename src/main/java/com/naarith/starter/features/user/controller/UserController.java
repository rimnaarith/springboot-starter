package com.naarith.starter.features.user.controller;

import com.naarith.starter.features.auth.security.UserPrincipal;
import com.naarith.starter.features.user.dto.*;
import com.naarith.starter.features.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    /// =========================
    /// Update Information
    /// =========================
    @Operation(summary = "Update logged in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation error.")
    })
    @PutMapping
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UserUpdateReqDTO reqDTO, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("Update user information attempt for emai={}", userPrincipal.getUsername());
        service.update(reqDTO, userPrincipal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /// =========================
    /// Get user details by email
    /// =========================
    @Operation(summary = "Get user by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("{email}")
    public ResponseEntity<UserDetailsResDTO> getUser(@PathVariable String email) {
        log.info("Get user information attempt for email={}", email);
        return ResponseEntity.ok(service.getUserDetailsByEmail(email));
    }

    /// =========================
    /// Get user list
    /// =========================
    @Operation(summary = "Get a list of users", description = "Get a list of users (only for users who have completed profile setup)")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation error.")
    })
    @GetMapping
    public ResponseEntity<UserListResDTO> getUserList(@Valid @ModelAttribute UserListReqDTO reqDTO) {
        log.info("Get user list request received data={}", reqDTO);
        return ResponseEntity.ok(service.getUserList(reqDTO));
    }
}
