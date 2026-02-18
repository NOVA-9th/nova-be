package com.nova.nova_server.domain.batch.common.repository;

import com.nova.nova_server.domain.batch.common.entity.AiBatchEntity;
import com.nova.nova_server.domain.batch.common.entity.AiBatchState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiBatchRepository extends JpaRepository<AiBatchEntity, Long> {
    List<AiBatchEntity> findAllByState(AiBatchState state);

    Optional<AiBatchEntity> findByBatchId(String batchId);
}
