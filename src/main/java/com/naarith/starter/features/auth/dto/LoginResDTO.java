package com.naarith.starter.features.auth.dto;

import lombok.Builder;

@Builder
public record LoginResDTO(
        String email,
        String accessToken,
        boolean isCompletedProfile
) {

}
