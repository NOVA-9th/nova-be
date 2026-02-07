package com.nova.nova_server.domain.keyword.service;

import com.nova.nova_server.domain.interest.entity.Interest;
import com.nova.nova_server.domain.interest.repository.InterestRepository;
import com.nova.nova_server.domain.keyword.dto.KeywordCreateRequest;
import com.nova.nova_server.domain.keyword.dto.KeywordResponse;
import com.nova.nova_server.domain.keyword.dto.KeywordUpdateRequest;
import com.nova.nova_server.domain.keyword.entity.Keyword;
import com.nova.nova_server.domain.keyword.error.KeywordErrorCode;
import com.nova.nova_server.domain.keyword.repository.KeywordRepository;
import com.nova.nova_server.global.apiPayload.exception.NovaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final InterestRepository interestRepository;

    @Transactional
    public KeywordResponse create(KeywordCreateRequest request) {
        Interest interest = interestRepository.findById(request.getInterestId())
                .orElseThrow(() -> new NovaException(KeywordErrorCode.INTEREST_NOT_FOUND_BAD_REQUEST));
        Keyword keyword = Keyword.of(interest, request.getName());
        Keyword saved = keywordRepository.save(keyword);
        return KeywordResponse.from(saved);
    }

    public KeywordResponse getById(Long id) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new NovaException(KeywordErrorCode.KEYWORD_NOT_FOUND));
        return KeywordResponse.from(keyword);
    }

    public List<KeywordResponse> getAll() {
        return keywordRepository.findAll().stream()
                .map(KeywordResponse::from)
                .toList();
    }

    @Transactional
    public KeywordResponse update(Long id, KeywordUpdateRequest request) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new NovaException(KeywordErrorCode.KEYWORD_NOT_FOUND));
        Interest interest = null;
        if (request.getInterestId() != null) {
            interest = interestRepository.findById(request.getInterestId())
                    .orElseThrow(() -> new NovaException(KeywordErrorCode.INTEREST_NOT_FOUND_BAD_REQUEST));
        }
        keyword.update(request.getName(), interest);
        return KeywordResponse.from(keyword);
    }

    @Transactional
    public void delete(Long id) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new NovaException(KeywordErrorCode.KEYWORD_NOT_FOUND));
        keywordRepository.delete(keyword);
    }
}
