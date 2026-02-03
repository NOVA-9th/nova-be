package com.nova.nova_server.global.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class OpenAIConfig {

    @Value("${ai.openai.project-id}")
    private String projectId;

    @Value("${ai.openai.key}")
    private String key;

    @Value("${ai.openai.model}")
    private String model;

    // 요약 일관성을 위해 낮은 값 권장
    @Value("${ai.openai.temperature:0}")
    private double temperature;

    @Value("${ai.openai.max-request-per-batch:50000}")
    private int maxRequestPerBatch;

    @Bean
    public OpenAIClient openAIClient() {
        return OpenAIOkHttpClient.builder()
                .apiKey(key)
                .project(projectId)
                .build();
    }

}
