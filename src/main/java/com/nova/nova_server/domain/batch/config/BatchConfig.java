package com.nova.nova_server.domain.batch.config;

import com.nova.nova_server.domain.batch.processor.ArticleSourceToArticleProcessor;
import com.nova.nova_server.domain.batch.reader.ArticleSourceItemReader;
import com.nova.nova_server.domain.batch.writer.ArticleItemWriter;
import com.nova.nova_server.domain.post.model.Article;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.service.ArticleApiServiceFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Article ingestion job: each ArticleApiService runs in parallel (Split).
 * One flow per provider; split flow runs all provider flows concurrently.
 */
@Configuration
public class BatchConfig {

    private static final String JOB_NAME = "articleIngestionJob";
    private static final String STEP_NAME_PREFIX = "articleIngestionStep-";
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job articleIngestionJob(
            JobRepository jobRepository,
            ArticleApiServiceFactory articleApiServiceFactory,
            TaskExecutor batchTaskExecutor,
            PlatformTransactionManager transactionManager,
            ArticleSourceItemReader articleSourceItemReader,
            ArticleSourceToArticleProcessor articleSourceToArticleProcessor,
            ArticleItemWriter articleItemWriter) {
        List<Flow> providerFlows = buildProviderFlows(
                articleApiServiceFactory,
                jobRepository,
                transactionManager,
                articleSourceItemReader,
                articleSourceToArticleProcessor,
                articleItemWriter);
        if (providerFlows.isEmpty()) {
            return new JobBuilder(JOB_NAME, jobRepository)
                    .start(emptyStep(jobRepository, transactionManager))
                    .build();
        }
        if (providerFlows.size() == 1) {
            return new JobBuilder(JOB_NAME, jobRepository)
                    .start(providerFlows.get(0))
                    .build()
                    .build();
        }
        Flow splitFlow = new FlowBuilder<Flow>("splitFlow")
                .split(batchTaskExecutor)
                .add(providerFlows.toArray(new Flow[0]))
                .build();
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(splitFlow)
                .build()
                .build();
    }

    private Step emptyStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("articleIngestionStep-empty", jobRepository)
                .<ArticleSource, Article>chunk(1, transactionManager)
                .reader(() -> null)
                .processor(item -> null)
                .writer(chunk -> { })
                .build();
    }

    private List<Flow> buildProviderFlows(
            ArticleApiServiceFactory articleApiServiceFactory,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ArticleSourceItemReader articleSourceItemReader,
            ArticleSourceToArticleProcessor articleSourceToArticleProcessor,
            ArticleItemWriter articleItemWriter) {
        List<Flow> flows = new ArrayList<>();
        for (ArticleApiService service : articleApiServiceFactory.createAllAvailableServices()) {
            String providerName = service.getProviderName();
            Step step = createProviderStep(
                    STEP_NAME_PREFIX + providerName,
                    providerName,
                    jobRepository,
                    transactionManager,
                    articleSourceItemReader,
                    articleSourceToArticleProcessor,
                    articleItemWriter);
            Flow flow = new FlowBuilder<SimpleFlow>("flow-" + providerName)
                    .start(step)
                    .build();
            flows.add(flow);
        }
        return flows;
    }

    private Step createProviderStep(
            String stepName,
            String providerName,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ArticleSourceItemReader articleSourceItemReader,
            ArticleSourceToArticleProcessor articleSourceToArticleProcessor,
            ArticleItemWriter articleItemWriter) {
        return new StepBuilder(stepName, jobRepository)
                .<ArticleSource, Article>chunk(CHUNK_SIZE, transactionManager)
                .reader(articleSourceItemReader)
                .processor(articleSourceToArticleProcessor)
                .writer(articleItemWriter)
                .listener(new ProviderContextListener(providerName))
                .build();
    }
}
