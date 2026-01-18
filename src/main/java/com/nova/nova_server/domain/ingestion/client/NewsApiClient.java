package com.nova.nova_server.domain.ingestion.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class NewsApiClient {

    @Value("${external.newsapi.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    public String fetchRawJson() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        String url = "https://newsapi.org/v2/everything"
                + "?from=" + sevenDaysAgo
                + "&sortBy=publishedAt"
                + "&apiKey=" + apiKey;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

