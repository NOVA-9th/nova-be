package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardNewsBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CardNewsBookmarkRepository extends JpaRepository<CardNewsBookmark, Long> {
    @Query("select cnb.cardNews.id from CardNewsBookmark cnb where cnb.member.id = :memberId and cnb.cardNews.id in :cardNewsIds")
    Set<Long> filterBookmarkedCardNewsIds(@Param("memberId") long memberId,
            @Param("cardNewsIds") List<Long> cardNewsIds);

    Optional<CardNewsBookmark> findByMemberIdAndCardNewsId(Long memberId, Long cardNewsId);

    boolean existsByMemberIdAndCardNewsId(Long memberId, Long cardNewsId);

    void deleteByMemberIdAndCardNewsId(Long memberId, Long cardNewsId);

    @Query("select cnb.cardNews from CardNewsBookmark cnb where cnb.memberId = :memberId and cnb.cardNews.title like %:title%")
    List<CardNews> findBookmarkedCardNewsByTitle(@Param("memberId") Long memberId, @Param("title") String title);
}
