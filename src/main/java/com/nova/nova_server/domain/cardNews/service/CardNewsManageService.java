package com.nova.nova_server.domain.cardNews.service;

import com.nova.nova_server.domain.cardNews.repository.CardNewsBookmarkRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsHiddenRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsKeywordRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRelevanceRepository;
import com.nova.nova_server.domain.cardNews.repository.CardNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CardNewsManageService {

    private final CardNewsRepository cardNewsRepository;
    private final CardNewsKeywordRepository cardNewsKeywordRepository;
    private final CardNewsBookmarkRepository cardNewsBookmarkRepository;
    private final CardNewsRelevanceRepository cardNewsRelevanceRepository;
    private final CardNewsHiddenRepository cardNewsHiddenRepository;

    @Transactional
    public void deleteCardNewsBatch(List<Long> cardNewsIds) {
        if (cardNewsIds == null || cardNewsIds.isEmpty()) {
            return;
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();
        for (Long id : cardNewsIds) {
            if (id != null) {
                uniqueIds.add(id);
            }
        }

        if (uniqueIds.isEmpty()) {
            return;
        }

        List<Long> targetIds = new ArrayList<>(uniqueIds);

        // FK 제약 때문에 자식 테이블부터 삭제
        cardNewsKeywordRepository.deleteAllByCardNewsIds(targetIds);
        cardNewsBookmarkRepository.deleteAllByCardNewsIds(targetIds);
        cardNewsRelevanceRepository.deleteAllByCardNewsIds(targetIds);
        cardNewsHiddenRepository.deleteAllByCardNewsIds(targetIds);

        cardNewsRepository.deleteAllByIdInBatch(targetIds);
    }
}
