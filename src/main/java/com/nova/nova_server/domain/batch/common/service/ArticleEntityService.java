package com.nova.nova_server.domain.batch.common.service;

import com.nova.nova_server.domain.batch.common.repository.ArticleEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleEntityService {
    private final ArticleEntityRepository articleEntityRepository;

    public Set<String> distinctUrls(Set<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return Set.of();
        }
        Set<String> existingUrls = articleEntityRepository.findUrlsByUrlIn(urls);
        return urls.stream()
                .filter(url -> !existingUrls.contains(url))
                .collect(Collectors.toSet());
    }
}
