package com.nova.nova_server.domain.batch.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 배치 작업 실행 메타데이터
 * - 증분 처리: last_run_at 기준 이후 발행된 아티클만 처리
 */
@Entity
@Table(name = "batch_run_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchRunMetadata extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "status", length = 20)
    private String status;

    @Builder
    public BatchRunMetadata(String jobName, LocalDateTime executedAt, String status) {
        this.jobName = jobName;
        this.executedAt = executedAt;
        this.status = status;
    }
}
