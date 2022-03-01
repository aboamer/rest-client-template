package com.amer.restclient.infrastructure.rest.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) Jumia.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientConfig {

    private static final int IDLE_CONNECTION_VERIFY_TIMER_MILLISECONDS = 30000;

    private final HttpClientProperties httpClientProperties;

    @Bean
    public CloseableHttpClient httpClient() {

        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(httpClientProperties.getReadTimeout())
                .setConnectTimeout(httpClientProperties.getConnectionTimeout())
                .setSocketTimeout(httpClientProperties.getSocketTimeOut()).build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {

        final PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(httpClientProperties.getMaxTotalConnections());

        return poolingConnectionManager;
    }

    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {

        return (httpResponse, httpContext) -> {

            final HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
            final HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);

            final Optional<Integer> keepAliveTimeFromHeaders = getKeepAliveTimeFromHeadersIfExists(elementIterator);

            return keepAliveTimeFromHeaders.orElse(httpClientProperties.getKeepAliveTime());
        };
    }

    private Optional<Integer> getKeepAliveTimeFromHeadersIfExists(final HeaderElementIterator elementIterator) {

        while (elementIterator.hasNext()) {

            final HeaderElement element = elementIterator.nextElement();
            final String param = element.getName();
            final String value = element.getValue();

            if (isValuePresent(param, value)) {
                return Optional.of(Integer.parseInt(value));
            }
        }
        return Optional.empty();
    }

    private boolean isValuePresent(final String param, final String value) {

        final String timeOutKey = "timeout";

        if (StringUtils.equalsIgnoreCase(timeOutKey, param)) {

            return StringUtils.isNumeric(value);
        }

        return false;
    }

    @Bean
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager pool) {

        return new IdleConnectionMonitorTask(pool, httpClientProperties);
    }

    @RequiredArgsConstructor
    private static final class IdleConnectionMonitorTask implements Runnable {

        private final PoolingHttpClientConnectionManager pool;
        private final HttpClientProperties httpClientProperties;

        @Override
        @Scheduled(fixedDelay = IDLE_CONNECTION_VERIFY_TIMER_MILLISECONDS)
        public void run() {

            if (Objects.nonNull(pool)) {

                pool.closeExpiredConnections();
                pool.closeIdleConnections(
                        httpClientProperties.getCloseIdleConnectionWaitTimeMilliseconds(),
                        TimeUnit.MILLISECONDS);
            }
        }
    }
}
