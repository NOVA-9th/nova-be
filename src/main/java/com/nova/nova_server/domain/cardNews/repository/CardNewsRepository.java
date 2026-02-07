package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardNewsRepository extends JpaRepository<CardNews, Long>, CardNewsRepositoryCustom {
    boolean existsByOriginalUrl(String originalUrl);
}
