package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardNewsRepository extends JpaRepository<CardNews, Long>, CardNewsRepositoryCustom {
    boolean existsByOriginalUrl(String originalUrl);
}
