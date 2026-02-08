package com.nova.nova_server.domain.post.sources.deepsearch;

import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeepSearchServiceImpl implements ArticleApiService {

    private final DeepSearchClient client;

    @Override
    public List<ArticleSource> fetchArticles() {
        String json = client.fetchRawJson();
        return DeepSearchParser.parse(json);
    }

    @Override
    public String getProviderName() {
        return "DeepSearch";
    }
}