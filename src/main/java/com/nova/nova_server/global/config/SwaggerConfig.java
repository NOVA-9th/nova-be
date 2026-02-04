package com.nova.nova_server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .components(
                new io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                            .name("Authorization")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
            .info(new Info()
                .title("NOVA Server API")
                .description("""
                        NOVA 프로젝트 백엔드 API 문서입니다.
                        - 본 문서는 NOVA 서버에서 제공하는 REST API 명세를 정의합니다.
                        - Authorization: Bearer {JWT} 형식으로 인증합니다.
                        """)
                .version("v1.0.0")
            );
    }
}