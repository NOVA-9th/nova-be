package com.nova.nova_server.domain.batch.common.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_batch")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiBatchEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String batchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @Setter
    private AiBatchState state = AiBatchState.PROGRESS;

    public static AiBatchEntity fromBatchId(String batchId) {
        return AiBatchEntity.builder()
                .batchId(batchId)
                .build();
    }
}
