package com.nova.nova_server.domain.post.sources.stackexchange;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.model.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StackExchangeServiceImpl implements ArticleApiService {

    private final StackExchangeClient client;

    // 카테고리별 태그 매핑 (대분류 16개)
    private static final Map<String, String> CATEGORY_TAGS = new LinkedHashMap<>();

    static {
        CATEGORY_TAGS.put("Mobile App", "android;ios;flutter;react-native;kotlin;swift");
        CATEGORY_TAGS.put("Web", "javascript;html;css;reactjs;vue.js;angular;typescript");
        CATEGORY_TAGS.put("Backend", "java;spring-boot;node.js;python;django;express");
        CATEGORY_TAGS.put("AI/ML", "machine-learning;deep-learning;tensorflow;pytorch;keras");
        CATEGORY_TAGS.put("Data Engineering", "data-engineering;hadoop;spark;kafka;etl");
        CATEGORY_TAGS.put("Data Analysis", "data-analysis;pandas;numpy;sql;tableau");
        CATEGORY_TAGS.put("Infra", "infrastructure;cloud;aws;azure;docker;kubernetes");
        CATEGORY_TAGS.put("Security", "computer-security;network-security;cryptography;oauth");
        CATEGORY_TAGS.put("Networking", "networking;tcp;ip;dns;http");
        CATEGORY_TAGS.put("Embedded Systems", "embedded;arduino;raspberry-pi;c");
        CATEGORY_TAGS.put("Blockchain", "blockchain;ethereum;smart-contracts;solidity");
        CATEGORY_TAGS.put("Computer Vision", "computer-vision;opencv;image-processing");
        CATEGORY_TAGS.put("Game Dev", "game-development;unity3d;unreal-engine");
        CATEGORY_TAGS.put("System Engineering", "systems-engineering;linux;bash;shell");
        CATEGORY_TAGS.put("QA", "testing;selenium;junit;qa;automation");
    }

    @Override
    public List<ArticleSource> fetchArticles() {
        Map<String, JsonNode> uniqueItems = new LinkedHashMap<>();

        // 전체 top 2개
        addUnique(uniqueItems, client.fetchQuestions(null, "month", 2));

        // 월간 top 1개
        addUnique(uniqueItems, client.fetchQuestions(null, "month", 1));

        // 각 주요 카테고리 1개(추후 논의 후 확장)
        fetchAndAdd(uniqueItems, "Mobile App", 1);
        fetchAndAdd(uniqueItems, "Web", 1);
        fetchAndAdd(uniqueItems, "Backend", 1);
        fetchAndAdd(uniqueItems, "AI/ML", 1);
        fetchAndAdd(uniqueItems, "Infra", 1);

        return StackExchangeParser.parse(new ArrayList<>(uniqueItems.values()));
    }

    // 특정 카테고리의 top 질문 가져오기
    private void fetchAndAdd(Map<String, JsonNode> map, String category, int limit) {
        String tags = CATEGORY_TAGS.get(category);
        if (tags != null) {
            addUnique(map, client.fetchQuestions(tags, "week", limit));
        }
    }

    private void addUnique(Map<String, JsonNode> map, List<JsonNode> items) {
        for (JsonNode item : items) {
            if (item.has("question_id")) {
                String id = item.get("question_id").asText();
                map.putIfAbsent(id, item);
            }
        }
    }

    @Override
    public String getProviderName() {
        return "StackExchange";
    }
}
