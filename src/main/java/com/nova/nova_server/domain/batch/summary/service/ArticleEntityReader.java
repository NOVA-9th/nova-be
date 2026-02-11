package com.nova.nova_server.domain.batch.summary.service;

import com.nova.nova_server.domain.batch.common.entity.ArticleEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.stereotype.Component;

@Component
public class ArticleEntityReader extends JpaPagingItemReader<ArticleEntity> {
    public ArticleEntityReader(EntityManagerFactory entityManagerFactory) {
        setName("articleEntityReader");
        setEntityManagerFactory(entityManagerFactory);
        setPageSize(1000);

        setQueryString(
                "SELECT a FROM ArticleEntity a " +
                        "WHERE a.state = 'STAGED' " +
                        "ORDER BY a.id ASC"
        );
    }

    @Override
    public int getPage() {
        return 0;
    }
}
