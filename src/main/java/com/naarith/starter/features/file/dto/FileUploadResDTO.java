package com.naarith.starter.features.file.dto;

import lombok.Builder;

@Builder
public record FileUploadResDTO(
        String fileId,
        String url
) {
}
