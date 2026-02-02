package com.nova.nova_server.domain.post.model;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Article(
        String title, //제목
        String content, //내용
        String author, //글쓴이
        String source, //글의 출처(ex.뉴스투데이, 조선일보 등)
        LocalDateTime publishedAt, //발행시간
        CardType cardType,//NEWS|JOBS|COMMUNITY
        String url //원본 url
) {}

