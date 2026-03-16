package com.naarith.starter.features.file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileWebMvcConfig implements WebMvcConfigurer {
    private final Path root;

    public FileWebMvcConfig(FileProperties fileProperties) {
        this.root = Paths.get(fileProperties.getUploadDir());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + root.toAbsolutePath().normalize() + "/")
                .setCachePeriod(3600);
    }
}
