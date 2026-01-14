package com.nova.nova_server.domain.keyword.repository;

import com.nova.nova_server.domain.keyword.entity.KeywordStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordStatisticsRepository extends JpaRepository<KeywordStatistics, Long> {
}
