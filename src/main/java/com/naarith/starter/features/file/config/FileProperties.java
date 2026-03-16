package com.naarith.starter.features.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "application.storage")
public class FileProperties {
    private String uploadDir;
}
