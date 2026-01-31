package com.nova.nova_server.domain.batch.converter;

import com.nova.nova_server.domain.post.model.Article;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Article → LLM 요약용 프롬프트 문자열 변환
 * 요약, 근거, 키워드 5개 추출을 위한 프롬프트 생성
 */
@Component
public class ArticleToPromptConverter {

    private static final String SUMMARY_PROMPT = """
        다음 기사 내용을 바탕으로 JSON 형태로 요약(summary), 근거(evidence), 키워드 5개(keywords)를 추출해주세요.
        반드시 아래 형식으로만 응답하세요: {"summary":"...", "evidence":"...", "keywords":["...","...","...","...","..."]}
        
        기사 내용:
        """;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Article 목록을 LLM 배치 입력용 문자열 목록으로 변환
     * 각 문자열 = 프롬프트 + 기사 내용 (title, content, source 등)
     *
     * @param articles 아티클 목록
     * @return 프롬프트+아티클 조합 문자열 목록 (custom_id: article-0, article-1, ... 순서)
     */
    public List<String> toPromptStrings(List<Article> articles) {
        List<String> result = new ArrayList<>();
        for (Article article : articles) {
            String articleText = formatArticle(article);
            result.add(SUMMARY_PROMPT + articleText);
        }
        return result;
    }

    private String formatArticle(Article article) {
        StringBuilder sb = new StringBuilder();
        sb.append("제목: ").append(nullToEmpty(article.title())).append("\n");
        sb.append("출처: ").append(nullToEmpty(article.source())).append("\n");
        sb.append("작성자: ").append(nullToEmpty(article.author())).append("\n");
        if (article.publishedAt() != null) {
            sb.append("발행일: ").append(article.publishedAt().format(FORMATTER)).append("\n");
        }
        sb.append("본문: ").append(nullToEmpty(article.content()));
        return sb.toString();
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
