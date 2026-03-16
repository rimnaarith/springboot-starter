package com.naarith.starter.features.auth.service.model;

import com.naarith.starter.features.auth.dto.LoginResDTO;
import lombok.Builder;

@Builder
public record LoginResult(
        LoginResDTO resDTO,
        String refreshToken
) {
}
