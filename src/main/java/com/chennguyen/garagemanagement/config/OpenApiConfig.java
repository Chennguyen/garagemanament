package com.chennguyen.garagemanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Garage Management API", version = "v1.0"),
        security = @SecurityRequirement(name = "bearerAuth") // 👈 Sửa thành bearerAuth
)
@SecurityScheme(
        name = "bearerAuth",            // 👈 Sửa thành bearerAuth (cho khớp với Controller)
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
    // Không cần nội dung, chỉ cần chú thích
}
