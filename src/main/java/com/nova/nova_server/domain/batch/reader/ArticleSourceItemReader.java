package com.nova.nova_server.domain.batch.reader;

import com.nova.nova_server.domain.batch.config.ProviderContextListener;
import com.nova.nova_server.domain.post.model.ArticleSource;
import com.nova.nova_server.domain.post.service.ArticleApiService;
import com.nova.nova_server.domain.post.service.ArticleApiServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Batch ItemReader: reads ArticleSource from a single ArticleApiService (provider).
 * Provider name is taken from step execution context (set by {@link ProviderContextListener}).
 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ArticleSourceItemReader implements ItemReader<ArticleSource> {

    private final ArticleApiServiceFactory articleApiServiceFactory;

    private List<ArticleSource> sources;
    private int index = 0;

    @Override
    public ArticleSource read() {
        if (sources == null) {
            String providerName = getProviderNameFromContext();
            sources = loadSourcesForProvider(providerName);
            log.info("ArticleSourceItemReader [{}] loaded {} ArticleSource(s)", providerName, sources.size());
        }
        if (index >= sources.size()) {
            return null;
        }
        return sources.get(index++);
    }

    private String getProviderNameFromContext() {
        StepContext context = StepSynchronizationManager.getContext();
        if (context == null) {
            throw new IllegalStateException("StepContext not available; ensure ProviderContextListener runs before this reader");
        }
        String name = context.getStepExecution().getExecutionContext().getString(ProviderContextListener.CONTEXT_KEY_PROVIDER_NAME);
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("providerName not set in step context; add ProviderContextListener with provider name");
        }
        return name;
    }

    private List<ArticleSource> loadSourcesForProvider(String providerName) {
        ArticleApiService service = articleApiServiceFactory.getServiceByName(providerName);
        try {
            return new ArrayList<>(service.fetchArticles());
        } catch (Exception e) {
            log.warn("Reader {} failed: {}", providerName, e.getMessage());
            return new ArrayList<>();
        }
    }
}
