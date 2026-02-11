package com.nova.nova_server.domain.post.sources.techblog;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.HtmlCleaner;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.model.SelfContainedArticleSource;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TechBlogParser {
    private static final DateTimeFormatter RSS_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<ArticleSource> parse(JsonNode rootNode) {
        if (rootNode == null || !rootNode.has("items")) {
            return Collections.emptyList();
        }

        List<ArticleSource> articles = new ArrayList<>();
        String blogName = "TechBlog";

        // Source 추출
        if (rootNode.has("feed") && rootNode.get("feed").has("title")) {
            blogName = rootNode.get("feed").get("title").asText();
        }

        // 게시글 목록 파싱
        JsonNode items = rootNode.get("items");
        for (JsonNode item : items) {
            String title = item.path("title").asText();
            String url = item.path("link").asText();
            String author = item.path("author").asText();

            // content가 있으면 쓰고, 없으면 description 사용
            String rawContent = item.has("content") ? item.get("content").asText() : "";
            if (rawContent.isEmpty() && item.has("description")) {
                rawContent = item.get("description").asText();
            }

            String cleanContent = HtmlCleaner.getTextFromHtml(rawContent);

            // 날짜 파싱
            LocalDateTime publishedAt = null;
            if (item.has("pubDate")) {
                String dateStr = item.get("pubDate").asText();
                try {
                    publishedAt = LocalDateTime.parse(dateStr, RSS_DATE_FORMATTER);
                } catch (Exception e) {
                    publishedAt = LocalDateTime.now();
                }
            }

            Article article = Article.builder()
                    .title(title)
                    .content(cleanContent)
                    .author(author.isEmpty() ? blogName : author)
                    .source(blogName)
                    .publishedAt(publishedAt)
                    .cardType(CardType.COMMUNITY)
                    .url(url)
                    .build();
            articles.add(new SelfContainedArticleSource(url, article));
        }

        return articles;
    }


}