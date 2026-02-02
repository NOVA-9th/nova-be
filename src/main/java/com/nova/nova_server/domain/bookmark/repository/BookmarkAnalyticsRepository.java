package com.nova.nova_server.domain.bookmark.repository;

import com.nova.nova_server.domain.bookmark.dto.BookmarkInterestCountResponse;
import com.nova.nova_server.domain.bookmark.dto.BookmarkSourceTypeCountResponse;
import com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkAnalyticsRepository extends JpaRepository<CardNewsBookmark, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT new com.nova.nova_server.domain.bookmark.dto.BookmarkInterestCountResponse(i.id, i.name, COUNT(DISTINCT cn.id)) "
            +
            "FROM CardNewsBookmark b " +
            "JOIN b.cardNews cn " +
            "JOIN CardNewsKeyword cnk ON cnk.cardNews.id = cn.id " +
            "JOIN cnk.keyword k " +
            "JOIN k.interest i " +
            "WHERE b.memberId = :memberId " +
            "GROUP BY i.id, i.name")
    java.util.List<BookmarkInterestCountResponse> findBookmarkCountsByInterest(
            @org.springframework.data.repository.query.Param("memberId") Long memberId);

    @org.springframework.data.jpa.repository.Query("SELECT new com.nova.nova_server.domain.bookmark.dto.BookmarkSourceTypeCountResponse(cn.cardType, COUNT(DISTINCT cn.id)) "
            +
            "FROM CardNewsBookmark b " +
            "JOIN b.cardNews cn " +
            "WHERE b.memberId = :memberId " +
            "GROUP BY cn.cardType")
    java.util.List<BookmarkSourceTypeCountResponse> findBookmarkCountsBySourceType(
            @org.springframework.data.repository.query.Param("memberId") Long memberId);
}
