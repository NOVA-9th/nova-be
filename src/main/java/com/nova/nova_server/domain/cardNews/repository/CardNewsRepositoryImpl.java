package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.dto.CardNewsIdScoreResult;
import com.nova.nova_server.domain.cardNews.dto.CardNewsScoreResult;
import com.nova.nova_server.domain.cardNews.dto.CardNewsSearchCondition;
import com.nova.nova_server.domain.cardNews.dto.QCardNewsIdScoreResult;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.nova.nova_server.domain.feed.enums.FeedSort;
import com.nova.nova_server.global.config.FeedConfig;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nova.nova_server.domain.cardNews.entity.QCardNews.cardNews;
import static com.nova.nova_server.domain.cardNews.entity.QCardNewsBookmark.cardNewsBookmark;
import static com.nova.nova_server.domain.cardNews.entity.QCardNewsKeyword.cardNewsKeyword;
import static com.nova.nova_server.domain.keyword.entity.QKeyword.keyword;
import static com.nova.nova_server.domain.member.entity.QMemberPreferKeyword.memberPreferKeyword;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CardNewsRepositoryImpl implements CardNewsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final FeedConfig feedConfig;

    @Override
    public Page<CardNewsScoreResult> searchByCondition(CardNewsSearchCondition condition) {

        // 검색 조건에 맞는 ID와 관련도 점수 조회
        List<CardNewsIdScoreResult> cardNewsIdScoreList = queryFactory
                .select(
                        new QCardNewsIdScoreResult(
                                cardNews.id,
                                // 최신순 정렬 시에도 관련도 점수는 표시되어야 함
                                calcTotalScore(condition.memberId())
                        )
                )
                .from(cardNews)
                .where(
                        cardTypeIn(condition.type()),
                        keywordIn(condition.keywords()),
                        publishedAfter(condition.startDate()),
                        publishedBefore(condition.endDate()),
                        isSaved(condition.memberId(), condition.saved())
                )
                .orderBy(getOrderSpecifiers(condition.memberId(), condition.sort()))
                .offset(condition.pageable().getOffset())
                .limit(condition.pageable().getPageSize())
                .fetch();

        // 검색 조건에 맞는 전체 개수 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(cardNews.count())
                .from(cardNews)
                .where(
                        cardTypeIn(condition.type()),
                        keywordIn(condition.keywords()),
                        publishedAfter(condition.startDate()),
                        publishedBefore(condition.endDate()),
                        isSaved(condition.memberId(), condition.saved())
                );

        log.debug("Fetched CardNews ids {} by condition {}", cardNewsIdScoreList, condition);

        if (cardNewsIdScoreList.isEmpty()) {
            return Page.empty(condition.pageable());
        }

        // 카드 뉴스 내용 조회
        List<CardNews> cardNewsList = queryFactory
                .select(cardNews).distinct()
                .from(cardNews)
                .leftJoin(cardNews.keywords, cardNewsKeyword).fetchJoin()
                .leftJoin(cardNewsKeyword.keyword, keyword).fetchJoin()
                .where(cardIdIn(cardNewsIdScoreList.stream().map(CardNewsIdScoreResult::cardNewsId).toList()))
                .fetch();

        // 카드 뉴스별 점수 매핑
        Map<Long, CardNews> cardNewsMap = cardNewsList.stream()
                .collect(Collectors.toMap(CardNews::getId, cardNews -> cardNews));

        List<CardNewsScoreResult> cardNewsScoreList = cardNewsIdScoreList.stream()
                .map(result -> CardNewsScoreResult.builder()
                        .cardNews(cardNewsMap.get(result.cardNewsId()))
                        .score(result.score())
                        .build())
                .toList();

        return PageableExecutionUtils.getPage(cardNewsScoreList, condition.pageable(), countQuery::fetchOne);
    }

    private BooleanExpression cardIdIn(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return null;
        }

        return cardNews.id.in(idList);
    }

    private BooleanExpression cardTypeIn(List<CardType> typeList) {
        if (typeList == null || typeList.isEmpty()) {
            return null;
        }

        return cardNews.cardType.in(typeList);
    }

    private BooleanExpression keywordIn(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return null;
        }

        return cardNews.id.in(
                JPAExpressions
                        .select(cardNewsKeyword.cardNews.id).distinct()
                        .from(cardNewsKeyword)
                        .join(cardNewsKeyword.keyword, keyword)
                        .where(keyword.name.in(keywords))
        );
    }

    private BooleanExpression publishedAfter(LocalDateTime date) {
        if (date == null) {
            return null;
        }

        return cardNews.publishedAt.goe(date);
    }

    private BooleanExpression publishedBefore(LocalDateTime date) {
        if (date == null) {
            return null;
        }

        return cardNews.publishedAt.loe(date);
    }

    private BooleanExpression isSaved(Long memberId, Boolean saved) {
        if (saved == null || !saved || memberId == null) {
            return null;
        }

        return cardNews.id.in(
                JPAExpressions
                        .select(cardNewsBookmark.cardNews.id)
                        .from(cardNewsBookmark)
                        .where(cardNewsBookmark.member.id.eq(memberId))
        );
    }

    @Override
    public Page<CardNews> searchBookmarked(Long memberId, String searchKeyword,
                                           org.springframework.data.domain.Pageable pageable) {
        // 필터링 및 정렬 수행
        List<Long> idList = queryFactory
                .select(cardNews.id)
                .from(cardNews)
                .where(
                        isSaved(memberId, true),
                        containsKeyword(searchKeyword))
                .orderBy(cardNews.publishedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(cardNews.count())
                .from(cardNews)
                .where(
                        isSaved(memberId, true),
                        containsKeyword(searchKeyword));

        if (idList.isEmpty()) {
            return Page.empty(pageable);
        }

        // 카드 뉴스 내용 조회
        List<CardNews> cardNewsList = queryFactory
                .select(cardNews).distinct()
                .from(cardNews)
                .leftJoin(cardNews.keywords, cardNewsKeyword).fetchJoin()
                .leftJoin(cardNewsKeyword.keyword, keyword).fetchJoin()
                .where(cardIdIn(idList))
                .fetch();

        return PageableExecutionUtils.getPage(cardNewsList, pageable, countQuery::fetchOne);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return cardNews.title.contains(keyword)
                .or(cardNews.summary.contains(keyword));
    }

    /**
     * 정렬 기준에 따른 OrderSpecifier 배열 반환
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(Long memberId, FeedSort sort) {
        if (sort == null) {
            sort = FeedSort.LATEST;
        }

        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if (sort == FeedSort.RELEVANCE) {
            orderSpecifiers.add(calcTotalScore(memberId).desc());
        }
        orderSpecifiers.add(cardNews.publishedAt.desc());

        return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);
    }

    /**
     * 최종 관련도 점수 계산
     */
    private NumberExpression<Integer> calcTotalScore(Long memberId) {
        if (memberId == null) {
            return Expressions.asNumber(feedConfig.getBaseScore());
        }

        return Expressions
                .asNumber(feedConfig.getBaseScore())
                .add(calcKeywordScore(memberId))
                .add(calcBookmarkScore(memberId));
    }

    /**
     * 키워드 기반 관련도 점수 계산<br/>
     * 사용자의 관심 키워드와 컨텐츠 키워드가 일치하는 개수에 가중치를 곱하여 점수 부여
     */
    private NumberExpression<Integer> calcKeywordScore(long memberId) {
        return Expressions.numberTemplate(
                Integer.class,
                "({0} * {1})",
                JPAExpressions
                        .select(cardNewsKeyword.count())
                        .from(cardNewsKeyword)
                        .where(
                                cardNewsKeyword.cardNews.id.eq(cardNews.id),
                                cardNewsKeyword.keyword.id.in(
                                        JPAExpressions
                                                .select(memberPreferKeyword.keyword.id)
                                                .from(memberPreferKeyword)
                                                .where(memberPreferKeyword.member.id.eq(memberId))
                                )
                        ),
                feedConfig.getKeywordMatchScore()
        );
    }

    /**
     * 북마크 기반 관련도 점수 계산<br/>
     * 사용자가 북마크한 카드뉴스와 키워드가 일치하는 개수에 가중치를 곱하여 점수 부여
     */
    private NumberExpression<Integer> calcBookmarkScore(long memberId) {
        return Expressions.numberTemplate(
                Integer.class,
                "({0} * {1})",
                JPAExpressions
                        .select(cardNewsKeyword.count())
                        .from(cardNewsKeyword)
                        .where(
                                cardNewsKeyword.cardNews.id.eq(cardNews.id),
                                cardNewsKeyword.keyword.id.in(
                                        JPAExpressions
                                                .select(cardNewsKeyword.keyword.id)
                                                .from(cardNewsKeyword)
                                                .leftJoin(cardNewsBookmark)
                                                .on(cardNewsBookmark.cardNews.id.eq(cardNewsKeyword.cardNews.id))
                                                .where(cardNewsBookmark.member.id.eq(memberId))
                                )
                        ),
                feedConfig.getBookmarkKeywordMatchScore()
        );
    }

}
