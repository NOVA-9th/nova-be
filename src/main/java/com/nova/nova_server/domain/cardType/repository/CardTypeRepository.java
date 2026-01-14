package com.nova.nova_server.domain.cardType.repository;

import com.nova.nova_server.domain.cardType.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardTypeRepository extends JpaRepository<CardType, Long> {
}
