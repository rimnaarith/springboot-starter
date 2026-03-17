package com.naarith.starter.features.user.dto;

import lombok.Builder;

@Builder
public record UserDTO(
        String uid,
        String email,
        String password,
        String firstName,
        String lastName,
        boolean isCompetedProfile,
        String profileImage
) {}
