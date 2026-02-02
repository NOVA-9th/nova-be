package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CardNewsBookmarkRepository extends JpaRepository<CardNewsBookmark, Long> {
    @Query("select cnb.cardNews.id from CardNewsBookmark cnb where cnb.member.id = :memberId and cnb.cardNews.id in :cardNewsIds")
    Set<Long> filterBookmarkedCardNewsIds(@Param("memberId") long memberId, @Param("cardNewsIds") List<Long> cardNewsIds);
}
