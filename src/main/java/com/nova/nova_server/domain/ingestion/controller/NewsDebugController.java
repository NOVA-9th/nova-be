package com.nova.nova_server.domain.ingestion.controller;

import com.nova.nova_server.domain.ingestion.model.Article;
import com.nova.nova_server.domain.ingestion.service.NewsIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/debug/news")
public class NewsDebugController {

    private final NewsIngestionService service;

    @GetMapping
    public List<Article> test() {
        return service.fetchArticles();
    }
}

