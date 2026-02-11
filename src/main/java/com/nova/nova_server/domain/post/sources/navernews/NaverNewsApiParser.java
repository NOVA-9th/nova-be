package com.nova.nova_server.domain.post.sources.navernews;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.model.SelfContainedArticleSource;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NaverNewsApiParser {

    private static final DateTimeFormatter NAVER_DATE =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public static List<ArticleSource> parse(JsonNode json) {
        List<ArticleSource> articles = new ArrayList<>();
        if (json == null || !json.has("items")) return articles;

        for (JsonNode item : json.get("items")) {
            String rawTitle = item.get("title").asText();
            String rawDescription = item.get("description").asText();
            String link = item.get("link").asText();
            String originalLink = item.has("originallink") && !item.get("originallink").asText().isEmpty()
                    ? item.get("originallink").asText()
                    : link;
            String pubDate = item.get("pubDate").asText();

            // HTML 엔티티 디코딩 + HTML 태그 제거
            String title = cleanHtml(rawTitle);
            String description = cleanHtml(rawDescription);

            // KST → UTC 변환
            LocalDateTime publishedAt = ZonedDateTime.parse(pubDate, NAVER_DATE)
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime();

            String source = resolveSourceFromLink(originalLink);

            Article article = Article.builder()
                    .title(title)
                    .content(description)
                    .author(null) // Naver News API는 author 정보 없음
                    .source(source)
                    .publishedAt(publishedAt)
                    .cardType(CardType.NEWS)
                    .url(link)
                    .build();
            articles.add(new SelfContainedArticleSource(link, article));
        }

        return articles;
    }

    /**
     * HTML 엔티티 디코딩 + HTML 태그 제거
     */
    private static String cleanHtml(String input) {
        if (input == null) return null;

        // 1. HTML 엔티티 디코딩 (&lt; → <, &gt; → >)
        String decoded = StringEscapeUtils.unescapeHtml4(input);

        // 2. HTML 태그 제거 (<b>, </b>, <strong> 등)
        String cleaned = decoded.replaceAll("<[^>]+>", "");

        // 3. 연속된 공백을 하나로
        return cleaned.replaceAll("\\s+", " ").trim();
    }

    private static String resolveSourceFromLink(String link) {
        try {
            URI uri = new URI(link);
            return uri.getHost();
        } catch (Exception e) {
            return "unknown";
        }
    }
}