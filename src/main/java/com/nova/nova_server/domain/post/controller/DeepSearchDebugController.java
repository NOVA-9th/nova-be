package com.nova.nova_server.domain.post.controller;

import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.service.DeepSearchIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * DeepSearch 데이터 ingestion 디버깅용 Controller
 */
@RestController
@RequiredArgsConstructor
public class DeepSearchDebugController {

    private final DeepSearchIngestionService service;

    @GetMapping("/debug/deepsearch")
    public List<Article> test() {
        return service.fetchArticles();
    }
}
