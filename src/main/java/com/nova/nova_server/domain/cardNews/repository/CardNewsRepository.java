package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CardNewsRepository extends JpaRepository<CardNews, Long>, CardNewsRepositoryCustom {
    boolean existsByOriginalUrl(String originalUrl);

    @Query("SELECT MIN(cn.createdAt) FROM CardNews cn")
    Optional<LocalDateTime> findMinCreatedAt();

    @Query("SELECT MAX(cn.createdAt) FROM CardNews cn")
    Optional<LocalDateTime> findMaxCreatedAt();
}
