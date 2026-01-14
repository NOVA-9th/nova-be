package com.nova.nova_server.domain.keyword.entity;

import com.nova.nova_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "keyword_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false, foreignKey = @ForeignKey(name = "FK_keyword_TO_stats"))
    private Keyword keyword;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "mention_count", nullable = false)
    private Integer mentionCount = 0;
}
