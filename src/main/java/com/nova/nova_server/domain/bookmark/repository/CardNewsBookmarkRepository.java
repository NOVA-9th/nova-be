package com.nova.nova_server.domain.bookmark.repository;

import com.nova.nova_server.domain.bookmark.dto.BookmarkInterestCountResponse;
import com.nova.nova_server.domain.bookmark.dto.BookmarkSourceTypeCountResponse;
import com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardNewsBookmarkRepository extends JpaRepository<CardNewsBookmark, Long> {

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

    @org.springframework.data.jpa.repository.Query("SELECT new com.nova.nova_server.domain.bookmark.dto.BookmarkSourceTypeCountResponse(ct.id, ct.name, COUNT(DISTINCT cn.id)) "
            +
            "FROM CardNewsBookmark b " +
            "JOIN b.cardNews cn " +
            "JOIN cn.cardType ct " +
            "WHERE b.memberId = :memberId " +
            "GROUP BY ct.id, ct.name")
    java.util.List<BookmarkSourceTypeCountResponse> findBookmarkCountsBySourceType(
            @org.springframework.data.repository.query.Param("memberId") Long memberId);

// 북마크 저장 api: 추후 피드조회 api 구현시 주석해제
//    @org.springframework.data.jpa.repository.Query("SELECT b FROM CardNewsBookmark b " +
//            "JOIN FETCH b.cardNews cn " +
//            "JOIN FETCH cn.cardType " +
//            "WHERE b.memberId = :memberId")
//    java.util.List<CardNewsBookmark> findAllByMemberIdWithCardNews(
//            @org.springframework.data.repository.query.Param("memberId") Long memberId);
}
