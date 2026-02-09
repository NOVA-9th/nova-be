package com.nova.nova_server.domain.batch.repository;

import com.nova.nova_server.domain.batch.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ArticleEntityRepository extends JpaRepository<ArticleEntity, Long> {
    boolean existsByUrl(String url);

    @Query("SELECT a.url FROM ArticleEntity a WHERE a.url IN :urls")
    Set<String> findUrlsByUrlIn(@Param("urls") Set<String> urls);
}
