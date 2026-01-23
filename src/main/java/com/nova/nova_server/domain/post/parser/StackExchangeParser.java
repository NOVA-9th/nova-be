package com.nova.nova_server.domain.post.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.CardType;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class StackExchangeParser {

    public List<Article> parse(List<JsonNode> items) {
        List<Article> articles = new ArrayList<>();

        for (JsonNode item : items) {
            try {
                // 제목
                String title = item.has("title") ? item.get("title").asText() : "No Title";

                // 작성자
                String author = "Unknown";
                if (item.has("owner") && item.get("owner").has("display_name")) {
                    author = item.get("owner").get("display_name").asText();
                }

                // URL
                String url = item.has("link") ? item.get("link").asText() : null;

                // 작성일
                LocalDateTime publishedAt = null;
                if (item.has("creation_date")) {
                    long timestamp = item.get("creation_date").asLong();
                    publishedAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
                }

                // LLM용 content 필드 매핑
                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append("[Stack Overflow Question]\n");

                if (item.has("score")) {
                    contentBuilder.append("Score: ").append(item.get("score").asInt()).append(" | ");
                }
                if (item.has("view_count")) {
                    contentBuilder.append("Views: ").append(item.get("view_count").asInt()).append("\n");
                }

                if (item.has("tags")) {
                    List<String> tags = StreamSupport.stream(item.get("tags").spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.toList());
                    contentBuilder.append("Tags: ").append(String.join(", ", tags)).append("\n");
                }

                // 질문 본문 처리
                if (item.has("body")) {
                    String cleanBody = cleanHtml(item.get("body").asText());
                    contentBuilder.append("Question Body:\n").append(cleanBody).append("\n\n");
                }

                // 답변 처리
                if (item.has("answers") && item.get("answers").isArray()) {
                    JsonNode answers = item.get("answers");

                    // top 답변 가져오기
                    if (answers.size() > 0) {
                        JsonNode topAnswer = answers.get(0);

                        contentBuilder.append("-----------------------------------\n");
                        contentBuilder.append("[Top Answer]\n");

                        // 답변 채택 여부 표시
                        if (topAnswer.has("is_accepted") && topAnswer.get("is_accepted").asBoolean()) {
                            contentBuilder.append("(Accepted) ");
                        }

                        // 답변 작성자
                        if (topAnswer.has("owner") && topAnswer.get("owner").has("display_name")) {
                            contentBuilder.append("By: ").append(topAnswer.get("owner").get("display_name").asText()).append("\n");
                        }

                        // 답변 본문
                        if (topAnswer.has("body")) {
                            String answerBody = cleanHtml(topAnswer.get("body").asText());
                            contentBuilder.append(answerBody);
                        }
                    }
                }

                String content = contentBuilder.toString();

                articles.add(Article.builder()
                        .title(title)
                        .content(content)
                        .author(author)
                        .source("Stack Overflow")
                        .publishedAt(publishedAt)
                        .cardType(CardType.COMMUNITY)
                        .url(url)
                        .build());

            } catch (Exception e) {
                System.err.println("StackExchange 아이템 파싱 실패: " + e.getMessage());
            }
        }

        return articles;
    }

    // HTML 태그 정제 헬퍼
    private String cleanHtml(String rawHtml) {
        String formatted = rawHtml
                .replaceAll("(?i)<br[^>]*>", "\n")
                .replaceAll("(?i)</p>", "\n\n")
                .replaceAll("(?i)</div>", "\n")
                .replaceAll("(?s)<pre>.*?</pre>", "[Code Block]") // 코드가 너무 길면 대체
                .replaceAll("<[^>]*>", "");

        return StringEscapeUtils.unescapeHtml4(formatted).trim();
    }
}
