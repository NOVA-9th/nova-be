package com.nova.nova_server.domain.batch.config;

import com.nova.nova_server.domain.batch.entity.ArticleEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaBatchConfig {
    @Bean
    public JpaItemWriter<ArticleEntity> articleEntityJpaItemWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<ArticleEntity>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }
}
