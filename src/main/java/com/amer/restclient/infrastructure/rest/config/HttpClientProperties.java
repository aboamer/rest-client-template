package com.amer.restclient.infrastructure.rest.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Copyright (c) Jumia.
 */
@Getter
@ConstructorBinding
@ConfigurationProperties
public class HttpClientProperties {

    private final int maxTotalConnections;
    private final int connectionTimeout;
    private final int readTimeout;
    private final int socketTimeOut;
    private final int keepAliveTime;
    private final long closeIdleConnectionWaitTimeMilliseconds;

    public HttpClientProperties(
            @DefaultValue("44") final int maxTotalConnections,
            @DefaultValue("30000") final int connectionTimeout,
            @DefaultValue("3000") final int connectionRequestTimeOut,
            @DefaultValue("60000") final int socketTimeOut,
            @DefaultValue("20000") final int keepAliveTime,
            @DefaultValue("45000") final int closeIdleConnectionWaitTime) {

        this.maxTotalConnections = maxTotalConnections;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = connectionRequestTimeOut;
        this.socketTimeOut = socketTimeOut;
        this.keepAliveTime = keepAliveTime;
        this.closeIdleConnectionWaitTimeMilliseconds = closeIdleConnectionWaitTime;
    }
}
