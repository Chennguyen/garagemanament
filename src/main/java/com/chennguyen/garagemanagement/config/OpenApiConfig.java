package com.chennguyen.garagemanagement.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "api", // Tên của scheme (sẽ hiện trong nút Authorize)
        type = SecuritySchemeType.HTTP, // Loại bảo mật là HTTP
        bearerFormat = "JWT",           // Định dạng token là JWT
        scheme = "bearer"               // Tiền tố là "Bearer " (Bắt buộc cho JWT)
)
public class OpenApiConfig {
    // Không cần nội dung, chỉ cần chú thích
}
