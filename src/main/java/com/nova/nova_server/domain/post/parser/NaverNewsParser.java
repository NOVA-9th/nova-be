package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.Article;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class NaverNewsParser {

    private static final DateTimeFormatter NAVER_DATE =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", java.util.Locale.ENGLISH);

    public List<Article> parse(JsonNode json) {
        List<Article> articles = new ArrayList<>();

        if (json == null || !json.has("items")) return articles;

        for (JsonNode item : json.get("items")) {

            String title = stripHtml(item.get("title").asText());
            String description = stripHtml(item.get("description").asText());
            String link = item.get("link").asText();
            String pubDate = item.get("pubDate").asText();

            LocalDateTime publishedAt =
                    ZonedDateTime.parse(pubDate, NAVER_DATE).toLocalDateTime();

            String source = resolveSourceFromLink(link);

            articles.add(new Article(
                    title,
                    description,
                    null,
                    source,
                    publishedAt,
                    link
            ));
        }
        return articles;
    }

    private String stripHtml(String input) {
        return input.replaceAll("<[^>]*>", "")
                .replace("&quot;", "\"")
                .replace("&amp;", "&");
    }

    private String resolveSourceFromLink(String link) {
        try {
            URI uri = new URI(link);
            return uri.getHost();
        } catch (Exception e) {
            return "unknown";
        }
    }
}

//@Component
//public class NaverNewsParser {
//
//    private static final DateTimeFormatter NAVER_DATE =
//            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", java.util.Locale.ENGLISH);
//
//    public List<Article> parse(JsonNode json) {
//        List<Article> articles = new ArrayList<>();
//
//        if (json == null || !json.has("items")) return articles;
//
//        for (JsonNode item : json.get("items")) {
//
//            String title = item.get("title").asText();
//            String description = item.get("description").asText();
//            String link = item.get("link").asText();
//            String pubDate = item.get("pubDate").asText();
//
//            LocalDateTime publishedAt = LocalDateTime.parse(pubDate, NAVER_DATE)
//                    .atZone(ZoneId.of("Asia/Seoul"))
//                    .toLocalDateTime();
//
//            // 언론사 추출
//            String source = resolveSourceFromLink(link);
//
//            articles.add(new Article(
//                    title,
//                    description, // content = description
//                    null,        // author 없음
//                    source,
//                    publishedAt,
//                    link
//            ));
//        }
//        return articles;
//    }
//
//    private String resolveSourceFromLink(String link) {
//        try {
//            URI uri = new URI(link);
//            return uri.getHost();
//        } catch (Exception e) {
//            return "unknown";
//        }
//    }
//}
