package com.planitsquare.holiday_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Holiday API", version = "1.0", description = "Holiday Service API")
)
public class SwaggerConfig {
}