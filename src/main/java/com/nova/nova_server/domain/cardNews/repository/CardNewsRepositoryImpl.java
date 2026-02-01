package com.nova.nova_server.domain.cardNews.repository;

import com.nova.nova_server.domain.cardNews.dto.CardNewsSearchCondition;
import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.nova.nova_server.domain.cardNews.entity.QCardNews.cardNews;
import static com.nova.nova_server.domain.cardNews.entity.QCardNewsBookmark.cardNewsBookmark;
import static com.nova.nova_server.domain.cardNews.entity.QCardNewsKeyword.cardNewsKeyword;
import static com.nova.nova_server.domain.keyword.entity.QKeyword.keyword;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CardNewsRepositoryImpl implements CardNewsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CardNews> searchByCondition(CardNewsSearchCondition condition) {

        // 필터링 및 정렬 수행
        List<Long> idList = queryFactory
                .select(cardNews.id)
                .from(cardNews)
                .where(
                        cardTypeEq(condition.type()),
                        keywordIn(condition.keywords()),
                        publishedAfter(condition.startDate()),
                        publishedBefore(condition.endDate()),
                        isSaved(condition.memberId(), condition.saved())
                )
                .orderBy(cardNews.publishedAt.desc())  // TODO: 관련도순 정렬 구현 예정
                .offset(condition.pageable().getOffset())
                .limit(condition.pageable().getPageSize())
                .fetch();

        // 전체 개수 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(cardNews.count())
                .from(cardNews)
                .where(
                        cardTypeEq(condition.type()),
                        keywordIn(condition.keywords()),
                        publishedAfter(condition.startDate()),
                        publishedBefore(condition.endDate()),
                        isSaved(condition.memberId(), condition.saved())
                );

        log.debug("Fetched CardNews ids {} by condition {}", idList, condition);

        if (idList.isEmpty()) {
            return Page.empty(condition.pageable());
        }

        // 카드 뉴스 내용 조회
        List<CardNews> cardNewsList = queryFactory
                .select(cardNews).distinct()
                .from(cardNews)
                .leftJoin(cardNews.keywords, cardNewsKeyword).fetchJoin()
                .leftJoin(cardNewsKeyword.keyword, keyword).fetchJoin()
                .where(cardIdIn(idList))
                .fetch();

        return PageableExecutionUtils.getPage(cardNewsList, condition.pageable(), countQuery::fetchOne);
    }

    private BooleanExpression cardIdIn(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return null;
        }

        return cardNews.id.in(idList);
    }

    private BooleanExpression cardTypeEq(CardType type) {
        if (type == null) {
            return null;
        }

        return cardNews.cardType.eq(type);
    }

    private BooleanExpression keywordIn(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return null;
        }

        return cardNews.id.in(
                JPAExpressions
                        .select(cardNewsKeyword.cardNewsId).distinct()
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
                        .select(cardNewsBookmark.cardNewsId)
                        .from(cardNewsBookmark)
                        .where(cardNewsBookmark.memberId.eq(memberId))
        );
    }

}
