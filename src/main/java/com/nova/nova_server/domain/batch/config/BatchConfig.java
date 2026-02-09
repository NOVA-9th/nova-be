package com.nova.nova_server.domain.batch.config;

import com.nova.nova_server.domain.batch.service.ArticleFlowFactory;
import com.nova.nova_server.domain.post.service.ArticleApiServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final ArticleApiServiceFactory apiServiceFactory;
    private final ArticleFlowFactory flowFactory;
    private final TaskExecutor flowTaskExecutor;

    @Bean
    public Job articleIngestionJob() {
        List<Flow> flows = flowFactory.createFlows(apiServiceFactory.createAllAvailableServices());

        Flow splitFlow = new FlowBuilder<SimpleFlow>("splitFlow")
                .split(flowTaskExecutor)
                .add(flows.toArray(new Flow[0]))
                .build();

        return new JobBuilder("articleIngestionJob", jobRepository)
                .start(splitFlow)
                .build()
                .build();
    }
}
