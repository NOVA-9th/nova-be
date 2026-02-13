package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNewsKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardNewsKeywordRepository extends JpaRepository<CardNewsKeyword, Long> {
    @Modifying
    @Query("DELETE FROM CardNewsKeyword k WHERE k.cardNewsId IN :cardNewsIds")
    void deleteAllByCardNewsIds(@Param("cardNewsIds") List<Long> cardNewsIds);
}
