package com.nova.nova_server.domain.cardNews.service;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardNewsHidden;
import com.nova.nova_server.domain.cardNews.error.CardNewsErrorCode;
import com.nova.nova_server.domain.cardNews.repository.CardNewsHiddenRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.global.apiPayload.code.error.CommonErrorCode;
import com.nova.nova_server.global.apiPayload.exception.NovaException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardNewsHiddenService {

    private final MemberRepository memberRepository;
    private final CardNewsRepository cardNewsRepository;
    private final CardNewsHiddenRepository cardNewsHiddenRepository;

    @Transactional
    public void hideCardNews(Long memberId, Long cardNewsId) {
        Member member = memberRepository.getReferenceById(memberId);
        CardNews cardNews = cardNewsRepository.findById(cardNewsId)
                .orElseThrow(() -> new NovaException(CardNewsErrorCode.CARDNEWS_NOT_FOUND));

        try {
            cardNewsHiddenRepository.save(
                    CardNewsHidden.builder()
                            .member(member)
                            .cardNews(cardNews)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(CardNewsHidden.UNIQUE_CONSTRAINT_NAME)) {
                throw new NovaException(CardNewsErrorCode.CARDNEWS_ALREADY_HIDDEN);
            }
            throw new NovaException(CommonErrorCode.BAD_REQUEST);
        }
    }

    @Transactional
    public void deleteAllHiddenCardNews(Long memberId) {
        cardNewsHiddenRepository.deleteAllByMemberId(memberId);
    }

}
