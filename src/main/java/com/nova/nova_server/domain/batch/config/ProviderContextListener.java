package com.nova.nova_server.domain.batch.config;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

/**
 * Sets provider name in step execution context so the step-scoped reader loads only that provider's ArticleSource.
 */
public class ProviderContextListener extends StepExecutionListenerSupport {

    public static final String CONTEXT_KEY_PROVIDER_NAME = "providerName";

    private final String providerName;

    public ProviderContextListener(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepExecution.getExecutionContext().putString(CONTEXT_KEY_PROVIDER_NAME, providerName);
    }
}
