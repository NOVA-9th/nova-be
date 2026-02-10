package com.nova.nova_server.domain.keyword.repository;

import com.nova.nova_server.domain.keyword.entity.KeywordStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface KeywordStatisticsRepository extends JpaRepository<KeywordStatistics, Long> {

    @Query("SELECT ks.keyword.id " +
            "FROM KeywordStatistics ks " +
            "WHERE ks.statDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ks.keyword.id " +
            "ORDER BY SUM(ks.mentionCount) DESC")
    List<Long> findTopKeywordIds(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 Pageable pageable);

    @Query("SELECT ks FROM KeywordStatistics ks " +
            "JOIN FETCH ks.keyword k " +
            "JOIN FETCH k.interest " +
            "WHERE ks.keyword.id IN :keywordIds " +
            "AND ks.statDate BETWEEN :startDate AND :endDate")

    List<KeywordStatistics> findByKeywordIdsInAndStatDateBetween(
            @Param("keywordIds") List<Long> keywordIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT k.interest.id, SUM(ks.mentionCount) as totalCount " +
            "FROM KeywordStatistics ks " +
            "JOIN ks.keyword k " +
            "GROUP BY k.interest.id " +
            "ORDER BY totalCount DESC")
    List<Object[]> findInterestRankingsAllTime();

    @Query("SELECT ks.keyword.name, SUM(ks.mentionCount) " +
            "FROM KeywordStatistics ks " +
            "WHERE ks.keyword.interest.id = :interestId " +
            "GROUP BY ks.keyword.id, ks.keyword.name " +
            "ORDER BY SUM(ks.mentionCount) DESC")
    List<Object[]> findTopKeywordsByInterestIdAllTime(@Param("interestId") Long interestId,
                                                      Pageable pageable);
}
