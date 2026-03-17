package com.naarith.starter.features.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateReqDTO(
        @NotBlank(message = "Firstname is required")
        String firstName,
        @NotBlank(message = "Lastname is required")
        String lastName,
        String profileImageId
) {
}
