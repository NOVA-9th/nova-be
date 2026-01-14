package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNewsRelevance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardNewsRelevanceRepository extends JpaRepository<CardNewsRelevance, Long> {
}
