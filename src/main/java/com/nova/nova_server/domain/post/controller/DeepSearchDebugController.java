package com.nova.nova_server.domain.post.controller;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.service.DeepSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeepSearchDebugController {

    private final DeepSearchService service;

    @GetMapping("/debug/deepsearch")
    public List<Article> test() {
        return service.fetchArticles();
    }
}
