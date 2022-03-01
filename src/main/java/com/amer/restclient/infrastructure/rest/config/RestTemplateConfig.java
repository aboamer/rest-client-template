package com.amer.restclient.infrastructure.rest.config;

import com.amer.restclient.infrastructure.rest.interceptor.LoggingClientHttpRequestInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Copyright (c) Jumia.
 */
@Configuration
@EnableConfigurationProperties(HttpClientProperties.class)
public class RestTemplateConfig {

    private final CloseableHttpClient httpClient;
    private final LoggingClientHttpRequestInterceptor loggingClientHttpRequestInterceptor;
    private final int poolSize;

    @Autowired
    public RestTemplateConfig(
            final HttpClientProperties httpClientProperties,
            final CloseableHttpClient httpClient,
            final LoggingClientHttpRequestInterceptor loggingClientHttpRequestInterceptor
    ) {

        this.httpClient = httpClient;
        this.loggingClientHttpRequestInterceptor = loggingClientHttpRequestInterceptor;
        this.poolSize = httpClientProperties.getMaxTotalConnections();
    }

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {

        return restTemplateBuilder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(clientHttpRequestFactory()))
                .additionalInterceptors(Collections.singletonList(loggingClientHttpRequestInterceptor))
                .build();
    }

    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {

        final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        clientHttpRequestFactory.setHttpClient(httpClient);

        return clientHttpRequestFactory;
    }

    @Bean
    public TaskScheduler httpConnectionClosingTaskScheduler() {

        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("idleMonitor");
        scheduler.setPoolSize(poolSize);

        return scheduler;
    }
}
