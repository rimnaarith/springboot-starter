package com.naarith.starter.features.user.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserListResDTO(
        int page,
        int size,
        int totalPage,
        long totalUser,

        List<UserDetailsResDTO> userList
) {
}
