package com.nova.nova_server.domain.post.controller;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.service.NewsApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/debug/newsapi")
@RequiredArgsConstructor
public class NewsAPIDebugController {

    private final NewsApiService service;

    @GetMapping
    public List<Article> test() {
        return service.fetchArticles();
    }
}
