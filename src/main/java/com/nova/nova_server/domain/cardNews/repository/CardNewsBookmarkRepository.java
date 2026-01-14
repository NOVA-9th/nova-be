package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardNewsBookmarkRepository extends JpaRepository<CardNewsBookmark, Long> {
}
