package com.nova.nova_server.domain.batch.repository;

import com.nova.nova_server.domain.batch.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleEntityRepository extends JpaRepository<ArticleEntity, Long> {
    boolean existsByUrl(String url);
}
