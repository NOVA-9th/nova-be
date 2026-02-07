package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.dto.CardNewsSearchCondition;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import org.springframework.data.domain.Page;

public interface CardNewsRepositoryCustom {
    Page<CardNews> searchByCondition(CardNewsSearchCondition condition);

    Page<CardNews> searchBookmarked(
            Long memberId,
            String searchKeyword,
            org.springframework.data.domain.Pageable pageable
    );
}
