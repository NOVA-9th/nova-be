package com.nova.nova_server.domain.batch.articleingestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Thread pool for batch parallel execution (Split + optional chunk-level parallelism).
 */
@Configuration
public class ExecutorConfig {
    @Bean
    public TaskExecutor flowTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15); // Provider가 9개이므로 여유있게
        executor.setMaxPoolSize(30);
        executor.setThreadNamePrefix("Flow-Mgr-");
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskExecutor stepTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20); // 동시에 몇 개의 글을 전처리할지 결정
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Worker-");
        executor.initialize();
        return executor;
    }
}
