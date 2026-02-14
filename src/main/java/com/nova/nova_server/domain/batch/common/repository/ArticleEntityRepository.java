package com.nova.nova_server.domain.batch.common.repository;

import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import com.nova.nova_server.domain.batch.common.entity.ArticleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ArticleEntityRepository extends JpaRepository<ArticleEntity, Long> {
    boolean existsByUrl(String url);

    @Query("SELECT a.url FROM ArticleEntity a WHERE a.url IN :urls")
    Set<String> findUrlsByUrlIn(@Param("urls") Set<String> urls);

    @Query("SELECT a.sourceUrl FROM ArticleEntity a WHERE a.sourceUrl IN :sourceUrls")
    Set<String> findSourceUrlsBySourceUrlIn(@Param("sourceUrls") Set<String> sourceUrls);

    List<ArticleEntity> findAllByIdIn(Set<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ArticleEntity a SET a.state = :state WHERE a.batchId = :batchId")
    int updateStateByBatchId(@Param("batchId") String batchId, @Param("state") ArticleState state);
}
