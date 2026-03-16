package com.naarith.starter.config;

import com.naarith.starter.exception.ApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("HTTP Client Demo API")
                        .version("1.0.0")
                        .description("Demonstrates HTTP client approaches in Spring Boot 4.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {

            /// Add shared error schema to all responses

            /// Register ApiError schema in components
            var schemas = openApi.getComponents().getSchemas();
            if (schemas == null) {
                schemas = new HashMap<>();
                openApi.getComponents().setSchemas(schemas);
            }

            schemas.putAll(
                    ModelConverters.getInstance().read(ApiError.class)
            );

            for (PathItem pathItem : openApi.getPaths().values()) {
                for (Operation operation : pathItem.readOperations()) {
                    for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
                        var status = entry.getKey();
                        var response = entry.getValue();

                        if (status.startsWith("4") || status.startsWith("5")) {
                            /// Set the response content schema for 4xx and 5xxx
                            var content = new Content()
                                    .addMediaType(
                                            org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiError"))
                                    );
                            response.setContent(content);
                        } else {
                            if (response.getContent() != null) {
                                /// For all other responses, set media type to application/json
                                Content content = new Content();
                                response.getContent().forEach((key, value) -> {
                                    if ("*/*".equals(key)) {
                                        content.addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, value);
                                    } else  {
                                        content.addMediaType(key, value);
                                    }
                                });
                                response.setContent(content);
                            }
                        }
                    }
                }
            }
        };
    }
}
