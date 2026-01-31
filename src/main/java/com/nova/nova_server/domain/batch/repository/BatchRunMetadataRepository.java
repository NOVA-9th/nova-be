package com.nova.nova_server.domain.batch.repository;

import com.nova.nova_server.domain.batch.entity.BatchRunMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BatchRunMetadataRepository extends JpaRepository<BatchRunMetadata, Long> {

    /**
     * 특정 job의 마지막 실행 시점 조회 (증분 처리용)
     */
    Optional<BatchRunMetadata> findTopByJobNameOrderByExecutedAtDesc(String jobName);
}
