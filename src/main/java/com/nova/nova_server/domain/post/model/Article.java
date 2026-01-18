package com.nova.nova_server.domain.post.model;

import java.time.LocalDateTime;
//아악 ㅅㅂ 출처 수정해야돼 다시 다시
public record Article(
        String title, //제목
        String content, //내용
        String author, //글쓴이
        String source, //출처
        LocalDateTime publishedAt, //발행시간
        String url //원본 url
) {}

