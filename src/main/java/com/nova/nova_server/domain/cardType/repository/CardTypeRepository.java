package com.nova.nova_server.domain.cardType.repository;

import com.nova.nova_server.domain.cardType.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardTypeRepository extends JpaRepository<CardType, Long> {

    Optional<CardType> findByName(String name);
}
