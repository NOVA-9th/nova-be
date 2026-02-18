package com.nova.nova_server.domain.batch.cardnews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardNewsBatchProcessingTasklet implements Tasklet {
    private final BatchProcessingService batchProcessingService;

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) {
        Object batchIdValue = chunkContext.getStepContext().getJobParameters().get("batchId");
        if (batchIdValue == null) {
            log.warn("batchId job parameter is missing");
            return RepeatStatus.FINISHED;
        }

        String batchId = batchIdValue.toString();
        batchProcessingService.processBatchResult(batchId);
        return RepeatStatus.FINISHED;
    }
}
