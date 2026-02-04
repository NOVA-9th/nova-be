package com.nova.nova_server.domain.cardNews.service;

import com.nova.nova_server.domain.cardNews.entity.CardNews;
import com.nova.nova_server.domain.cardNews.entity.CardNewsHidden;
import com.nova.nova_server.domain.cardNews.error.CardNewsErrorCode;
import com.nova.nova_server.domain.cardNews.repository.CardNewsHiddenRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.global.apiPayload.exception.NovaException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardNewsHiddenService {

    private final MemberRepository memberRepository;
    private final CardNewsRepository cardNewsRepository;
    private final CardNewsHiddenRepository cardNewsHiddenRepository;

    @Transactional
    public void hideCardNews(Long memberId, Long cardNewsId) {
        if (cardNewsHiddenRepository.existsByMemberIdAndCardNewsId(memberId, cardNewsId)) {
            throw new NovaException(CardNewsErrorCode.CARDNEWS_ALREADY_HIDDEN);
        }

        Member member = memberRepository.getReferenceById(memberId);
        CardNews cardNews = cardNewsRepository.findById(cardNewsId)
                .orElseThrow(() -> new NovaException(CardNewsErrorCode.CARDNEWS_NOT_FOUND));

        cardNewsHiddenRepository.save(
                CardNewsHidden.builder()
                        .member(member)
                        .cardNews(cardNews)
                        .build()
        );
    }

}
