package com.nova.nova_server.domain.batch.articleingestion.service;

import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import com.nova.nova_server.domain.batch.common.repository.ArticleEntityRepository;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleFlowFactory {

    private static final String FLOW_NAME_PREFIX = "articleIngestionFlow-";
    private static final String STEP_NAME_PREFIX = "articleIngestionStep-";
    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ArticleFetchProcessor articleProcessor;
    private final JpaItemWriter<ArticleEntity> articleWriter;
    private final ArticleEntityRepository articleEntityRepository;

    private final TaskExecutor flowTaskExecutor;
    private final TaskExecutor stepTaskExecutor;

    public List<Flow> createFlows(List<ArticleApiService> services) {
        return services.stream()
                .map(service -> {
                    Step step = createStep(service);
                    String flowName = FLOW_NAME_PREFIX + service.getProviderName();

                    return (Flow)new FlowBuilder<SimpleFlow>(flowName)
                            .start(step).build();
                })
                .toList();
    }

    public Flow createCombinedFlow(List<ArticleApiService> services) {
        List<Flow> flows = createFlows(services);

        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(flowTaskExecutor)
                .add(flows.toArray(new Flow[0]))
                .build();
    }

    private Step createStep(ArticleApiService service) {
        ItemStreamReader<ArticleSource> reader = new ArticleSourceItemReader(service, articleEntityRepository);

        return new StepBuilder(STEP_NAME_PREFIX + service.getProviderName(), jobRepository)
                .<ArticleSource, ArticleEntity>chunk(CHUNK_SIZE, transactionManager)
                .reader(new SynchronizedItemStreamReaderBuilder<ArticleSource>()
                        .delegate(reader)
                        .build())
                .processor(articleProcessor)
                .writer(articleWriter)
                .taskExecutor(stepTaskExecutor)
                .build();
    }
}