package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNewsKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardNewsKeywordRepository extends JpaRepository<CardNewsKeyword, Long> {
}
