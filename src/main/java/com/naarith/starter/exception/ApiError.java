package com.naarith.starter.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Schema(name = "ApiError", description = "Standard error response")
public record ApiError(
        int status,
        String message,
        @Nullable Map<String, String> errors
) {
    public String toJson() {
        String errorsJson = errors != null ?
                errors.entrySet().stream()
                        .map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\"")
                        .collect(Collectors.joining(",")) :
                null;

        return String.format(
                "{\"status\":%d,\"message\":\"%s\",\"errors\":%s}",
                status, message, errors != null ? "{" + errorsJson + "}" : null
        );
    }
}
