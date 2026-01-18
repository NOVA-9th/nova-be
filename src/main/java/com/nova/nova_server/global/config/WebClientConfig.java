package com.nova.nova_server.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "newsApiWebClient")
    public WebClient newsApiWebClient(
            @Value("${external.newsapi.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean(name = "newsDataWebClient")
    public WebClient newsDataWebClient(
            @Value("${external.newsdata.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean(name = "deepSearchWebClient")
    public WebClient deepSearchWebClient(
            @Value("${external.deepsearch.base-url}") String baseUrl
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl) // https://api-v2.deepsearch.com/v1
                .build();
    }

    @Bean(name = "naverNewsWebClient")
    public WebClient naverNewsWebClient(
            @Value("${external.navernews.base-url}") String baseUrl,
            @Value("${external.navernews.client-id}") String clientId,
            @Value("${external.navernews.client-secret}") String clientSecret
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

}