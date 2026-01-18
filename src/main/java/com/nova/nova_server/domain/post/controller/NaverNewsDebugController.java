package com.nova.nova_server.domain.post.controller;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.service.NewsDataIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NaverNewsDebugController {
    private final NewsDataIngestionService service;

    @GetMapping("/debug/naver")
    public List<Article> debug() {
        return service.fetchArticles();
    }
}
