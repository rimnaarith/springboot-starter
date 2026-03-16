package com.naarith.starter.features.file.service.model;

import lombok.Builder;

@Builder
public record StoredFile(
        String path,
        long size,
        String contentType,
        String extension,
        String originalName
) {
}
