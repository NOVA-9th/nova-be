package com.nova.nova_server.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("NOVA Server API")
                .description("""
                        NOVA 프로젝트 백엔드 API 문서입니다.
                        - 본 문서는 NOVA 서버에서 제공하는 REST API 명세를 정의합니다.
                        """)
                .version("v1.0.0")
            );
    }
}