package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNewsHidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardNewsHiddenRepository extends JpaRepository<CardNewsHidden, Long> {

    boolean existsByMemberIdAndCardNewsId(Long memberId, Long cardNewsId);

    void deleteAllByMemberId(Long memberId);

    @Modifying
    @Query("DELETE FROM CardNewsHidden h WHERE h.cardNews.id IN :cardNewsIds")
    void deleteAllByCardNewsIds(@Param("cardNewsIds") List<Long> cardNewsIds);

}
