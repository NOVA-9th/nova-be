package com.nova.nova_server.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class FeedConfig {

    @Value("${feed.max-page-size:10}")
    private int maxPageSize;

}
