package com.nova.nova_server.domain.post.model;

import java.time.LocalDateTime;

public record Article(
        String title, //제목
        String content, //내용
        String author, //글쓴이
        String source, //출처(ex.newsapi, newdata... 등)
        LocalDateTime publishedAt, //발행시간
        String url //원본 url
) {}

