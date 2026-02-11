package com.nova.nova_server.domain.post.model;

// 글 URL + 글을 어떻게 가져와야할 지 로직
public interface ArticleSource {
    String getUrl(); // 원문 URL
    Article fetchArticle(); // 글 읽어오기
}
