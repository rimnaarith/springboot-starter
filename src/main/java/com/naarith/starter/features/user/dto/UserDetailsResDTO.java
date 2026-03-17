package com.naarith.starter.features.user.dto;

import lombok.Builder;

@Builder
public record UserDetailsResDTO(
        String uid,
        String email,
        String firstName,
        String lastName,
        boolean isCompetedProfile,
        String profileImage
) {}
