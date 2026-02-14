package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.entity.CardNewsRelevance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardNewsRelevanceRepository extends JpaRepository<CardNewsRelevance, Long> {

	@Modifying
	@Query("DELETE FROM CardNewsRelevance r WHERE r.memberId = :memberId")
	void deleteAllByMemberId(@Param("memberId") Long memberId);

	@Modifying
	@Query("DELETE FROM CardNewsRelevance r WHERE r.cardNewsId IN :cardNewsIds")
	void deleteAllByCardNewsIds(@Param("cardNewsIds") java.util.List<Long> cardNewsIds);
}
