package com.nova.nova_server.domain.post.sources.github;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.model.SelfContainedArticleSource;
import com.nova.nova_server.domain.post.sources.github.dto.GitHubArticle;

import java.util.ArrayList;
import java.util.List;

public class GitHubParser {
    public static Article toArticle(GitHubArticle article, String readme) {
        String title = firstNonEmpty(article.fullName(), article.name(), "");
        String content = buildContent(article, readme);
        return Article.builder()
                .title(title)
                .content(content)
                .author(article.getAuthor())
                .source("GitHub")
                .publishedAt(article.createdAt())
                .cardType(CardType.COMMUNITY)
                .url(article.htmlUrl())
                .build();
    }

    private static String buildContent(GitHubArticle article, String readme) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder
                .append("[Repository Description]\n")
                .append(firstNonEmpty(article.description(), "No description provided."))
                .append("\n\n")
                .append("[Metadata]\n")
                .append("- Language: ")
                .append(firstNonEmpty(article.language(), "Unknown"))
                .append("\n")
                .append("- Stars: ")
                .append(article.stargazersCount())
                .append("\n");

        if (!article.topics().isEmpty()) {
            contentBuilder
                    .append("- Topics: ")
                    .append(String.join(", ", article.topics()))
                    .append("\n");
        }

        if (readme != null && !readme.isEmpty()) {
            contentBuilder
                    .append("\n[README]\n")
                    .append(cleanMarkdown(readme))
                    .append("\n");
        }
        return contentBuilder.toString();
    }

    private static String cleanMarkdown(String markdown) {
        if (markdown == null || markdown.isEmpty())
            return "";
        String noComment = markdown.replaceAll("<!--[\\s\\S]*?-->", "");
        String noHtml = noComment.replaceAll("<[^>]+>", "");
        String noImages = noHtml.replaceAll("!\\[.*?\\]\\(.*?\\)", "");
        String noLinks = noImages.replaceAll("\\[(.*?)\\]\\(.*?\\)", "$1");
        return noLinks.replaceAll("\\n{3,}", "\n\n").trim();
    }

    private static String firstNonEmpty(String... values) {
        for (String v : values) {
            if (v != null && !v.isEmpty()) {
                return v;
            }
        }
        return "";
    }
}
