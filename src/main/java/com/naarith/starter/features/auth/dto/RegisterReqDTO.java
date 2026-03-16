package com.naarith.starter.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterReqDTO(
        @NotBlank(message = "Email is required")
        @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}", message = "Email is not valid")
        String email,
        @NotBlank(message = "Password is required")
        String password
) {}
