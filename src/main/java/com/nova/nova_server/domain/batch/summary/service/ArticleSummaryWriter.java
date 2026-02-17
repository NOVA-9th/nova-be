package com.nova.nova_server.domain.batch.summary.service;

import com.nova.nova_server.domain.ai.service.AiBatchService;
import com.nova.nova_server.domain.batch.common.entity.AiBatchEntity;
import com.nova.nova_server.domain.batch.common.repository.AiBatchRepository;
import com.nova.nova_server.domain.batch.summary.converter.PromptConverter;
import com.nova.nova_server.domain.batch.summary.dto.LlmSummaryResult;
import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import com.nova.nova_server.domain.batch.common.entity.ArticleState;
import com.nova.nova_server.domain.batch.common.repository.ArticleEntityRepository;
import com.nova.nova_server.domain.batch.cardnews.service.CardNewsSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleSummaryWriter implements ItemWriter<ArticleEntity> {
    private final AiBatchService aiBatchService;
    private final ArticleEntityRepository articleEntityRepository;
    private final AiBatchRepository aiBatchRepository;

    @Override
    public void write(Chunk<? extends ArticleEntity> chunk) {
        if (chunk.isEmpty()) {
            return;
        }

        log.info("ArticleSummaryWriter: Processing chunk of {} items", chunk.size());

        Map<String, String> prompts = PromptConverter.toPromptMap(chunk.getItems());
        String batchId = aiBatchService.createBatch(prompts, LlmSummaryResult.class);
        aiBatchRepository.save(AiBatchEntity.fromBatchId(batchId));
        log.info("Batch submitted. BatchId: {}, Count: {}", batchId, chunk.size());

        for (ArticleEntity entity : chunk) {
            entity.setState(ArticleState.REQUESTED);
            entity.setBatchId(batchId);
        }

        articleEntityRepository.saveAll(chunk);
    }
}