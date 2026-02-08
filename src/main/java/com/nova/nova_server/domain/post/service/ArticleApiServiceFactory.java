package com.nova.nova_server.domain.post.service;

import com.nova.nova_server.domain.post.sources.techblog.TechBlogServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleApiServiceFactory {
    private final TechBlogServiceFactory techBlogServiceFactory;
    private final List<ArticleApiService> articleApiServices;

    public List<ArticleApiService> createAllAvailableServices() {
        List<ArticleApiService> services = new ArrayList<>();
        services.addAll(articleApiServices);
        services.addAll(techBlogServiceFactory.createAllAvailableServices());
        return services;
    }

    public ArticleApiService getServiceByName(String name) {
        return createAllAvailableServices().stream()
                .filter(service -> service.getProviderName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown provider: " + name));
    }
}
