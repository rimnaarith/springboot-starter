package com.naarith.starter.features.user.dto;

import jakarta.validation.constraints.Min;

public record UserListReqDTO(

        @Min(value = 1, message = "Page must be 1 or higher")
        Integer page,
        @Min(value = 5, message = "Size must be 5 or higher")
        Integer size
) {
    public UserListReqDTO {
        if (page == null) page = 1;
        if (size == null) size = 5;
    }
}
