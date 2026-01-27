package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.CardType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class GitHubParser {

    public List<Article> parse(List<JsonNode> items) {
        List<Article> articles = new ArrayList<>();

        for (JsonNode item : items) {
            try {
                // 레포 제목
                String title = item.has("full_name") ? item.get("full_name").asText() : "";
                if (title.isEmpty() && item.has("name")) {
                    title = item.get("name").asText();
                }

                // 레포 소유자
                String author = "";
                if (item.has("owner") && item.get("owner").has("login")) {
                    author = item.get("owner").get("login").asText();
                }

                // 메타데이터 추출
                String description = item.has("description") && !item.get("description").isNull()
                        ? item.get("description").asText()
                        : "No description provided.";

                String language = item.has("language") && !item.get("language").isNull()
                        ? item.get("language").asText()
                        : "Unknown";

                int stars = item.has("stargazers_count") ? item.get("stargazers_count").asInt() : 0;

                List<String> topics = new ArrayList<>();
                if (item.has("topics")) {
                    topics = StreamSupport.stream(item.get("topics").spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.toList());
                }

                // Content로 변환 (LLM에 들어갈 요약 정보 생성)
                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append("[Repository Description]\n").append(description).append("\n\n");
                contentBuilder.append("[Metadata]\n");
                contentBuilder.append("- Language: ").append(language).append("\n");
                contentBuilder.append("- Stars: ").append(stars).append("\n");
                if (!topics.isEmpty()) {
                    contentBuilder.append("- Topics: ").append(String.join(", ", topics)).append("\n");
                }

                // README 추가
                if (item.has("readme_content")) {
                    String readme = item.get("readme_content").asText();
                    if (readme != null && !readme.isEmpty()) {
                        String cleanedReadme = cleanMarkdown(readme);
                        contentBuilder.append("\n[README]\n").append(cleanedReadme).append("\n");
                    }
                }

                String content = contentBuilder.toString();

                // 레포 url
                String url = item.has("html_url") ? item.get("html_url").asText() : null;

                // 작성일
                LocalDateTime publishedAt = null;
                if (item.has("created_at")) {
                    String dateStr = item.get("created_at").asText();
                    if (dateStr != null && !dateStr.isEmpty()) {
                        publishedAt = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
                    }
                }

                articles.add(Article.builder()
                        .title(title)
                        .content(content)
                        .author(author)
                        .source("GitHub")
                        .publishedAt(publishedAt)
                        .cardType(CardType.COMMUNITY)
                        .url(url)
                        .build());

            } catch (Exception e) {
                System.err.println("GitHub 파싱 오류 " + e.getMessage());
            }
        }

        return articles;
    }

    // 마크다운 정제
    private String cleanMarkdown(String markdown) {
        if (markdown == null || markdown.isEmpty())
            return "";
        // HTML 주석
        String noComment = markdown.replaceAll("<!--[\\s\\S]*?-->", "");
        // HTML 태그
        String noHtml = noComment.replaceAll("<[^>]+>", "");
        // 이미지 삭제
        String noImages = noHtml.replaceAll("!\\[.*?\\]\\(.*?\\)", "");
        // 링크 텍스트 추출
        String noLinks = noImages.replaceAll("\\[(.*?)\\]\\(.*?\\)", "$1");
        // 연속공백&줄바꿈
        return noLinks.replaceAll("\\n{3,}", "\n\n").trim();
    }
}